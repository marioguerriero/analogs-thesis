library(DBI)
library(RMySQL)

# Not available value for database queries
NAV <- "#NA"

# Database connection parameters
dbuser <- "thesis"
dbpassword <- "thesis"
dbname <- "loganalysis"
dbhost <- "localhost"
dbport <- 3306

###############################
# Merge resource table with eavs
###############################
buildTables <- function(sourcedb=NULL) {
  dbconnectionurl <- paste("jdbc:mysql://",dbhost,":",dbport,"/",sourcedb,"?useSSL=false",sep="")
  con <<- dbConnect(RMySQL(),url=dbconnectionurl,user=dbuser,password=dbpassword)

  resources <<- buildResourcesTable()
  users <<- buildUsersTable()
  actions <<- buildActionsTable()
}

###############################
# Merge resource table with rav
###############################
buildResourcesTable <- function() {
  table <- "resource"
  eav <- "resourceproperty"

  # Query resources
  sql <- paste("select * from",table)
  q <- dbSendQuery(con,sql)
  entity <- fetch(q,n=-1)

  # Get rav table
  sql <- paste("select * from",eav)
  q <- dbSendQuery(con,sql)
  eav <- fetch(q,n=-1)

  # add columns for attributes
  attrs <- unique(eav$attribute)
  entity[,paste(table,".",attrs,sep="")] <- NAV

  # Add resources attributes
  for(id in entity[,"idResource"]) {

    # Get rav table
    sql <- paste("select * from ","resourceproperty","where entityId =",id)
    q <- dbSendQuery(con,sql)
    res <- fetch(q,n=-1)

    id <- as.numeric(id)

    for(a in res$attribute) {
      entity[which(entity$idResource == id),paste(table,".",a,sep="")] <- res$value[which(res$attribute == a)]
    }

  }

  #resources <<- entity
  return(entity)
}

###############################
# Merge user table with uav
###############################
buildUsersTable <- function() {
  table <- "user"
  eav <- "userproperty"

  # Query resources
  sql <- paste("select * from",table)
  q <- dbSendQuery(con,sql)
  entity <- fetch(q,n=-1)

  # Get rav table
  sql <- paste("select * from",eav)
  q <- dbSendQuery(con,sql)
  eav <- fetch(q,n=-1)

  # add columns for attributes
  attrs <- unique(eav$attribute)
  entity[,paste(table,".",attrs,sep="")] <- NAV

  # Add users attributes
  for(id in entity[,"idUser"])  {

    # Get uav table
    sql <- paste("select * from ","userproperty","where entityId =",id)
    q <- dbSendQuery(con,sql)
    res <- fetch(q,n=-1)

    id <- as.numeric(id)

    for(a in res$attribute) {
      entity[which(entity$idUser == id),paste(table,".",a,sep="")] <- res$value[which(res$attribute == a)]
    }
  }

  # Add users types
  # Get usertype table
  sql <- paste("select * from","usertype")
  q <- dbSendQuery(con,sql)
  types <- fetch(q,n=-1)

  entity[,paste(table,".","role",sep="")] <- NAV

  for(idUser in types$idUser) {
    sql <- paste("select types from","usertype","where","idUser","=",as.numeric(idUser))
    q <- dbSendQuery(con,sql)
    t <- fetch(q,n=-1)

    if(length(t$types) > 0) {
      type <- ""
      for(i in t$types) type<-paste(i,type,sep=",")
      type <- substr(type,1,nchar(type)-1)
      entity[which(entity$idUser == idUser),paste(table,".","role",sep="")] <- type
    }
  }

  #users <<- entity
  return(entity)
}

###############################
# Merge action table with aav
###############################
buildActionsTable <- function() {
  table <- "action"
  eav <- "actionproperty"

  # Query resources
  sql <- paste("select * from",table)
  q <- dbSendQuery(con,sql)
  entity <- fetch(q,n=-1)

  # Get rav table
  sql <- paste("select * from",eav)
  q <- dbSendQuery(con,sql)
  eav <- fetch(q,n=-1)

  # add columns for attributes
  attrs <- unique(eav$attribute)
  entity[,paste(table,".",attrs,sep="")] <- NAV

  # Add resources attributes
  for(id in entity[,"idAction"])  {

    # Get rav table
    sql <- paste("select * from ","actionproperty","where entityId =",id)
    q <- dbSendQuery(con,sql)
    res <- fetch(q,n=-1)

    id <- as.numeric(id)

    for(a in res$attribute) {
      entity[which(entity$idAction == id),paste(table,".",a,sep="")] <- res$value[which(res$attribute == a)]
    }
  }

  #actions <<- entity
  return(entity)
}

getUsers <- function() {
  return(users)
}

getResources <- function() {
  return(resources)
}

getActions <- function() {
  return(actions)
}
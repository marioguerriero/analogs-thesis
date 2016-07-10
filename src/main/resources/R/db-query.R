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

# Create database connection

###############################
# Merge resource table with rav
###############################
buildTables <- function(sourcedb=NULL) {
  dbconnectionurl <- paste("jdbc:mysql://",dbhost,":",dbport,"/",sourcedb,sep="")
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
  eav <- "rav"

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
    sql <- paste("select * from ","rav","where entityId =",id)
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
buildUsersTable <- function(sourcedb=NULL) {
  table <- "user"
  eav <- "uav"

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
  for(id in entity[,"idUser"])  {

    # Get rav table
    sql <- paste("select * from ","uav","where entityId =",id)
    q <- dbSendQuery(con,sql)
    res <- fetch(q,n=-1)

    id <- as.numeric(id)

    for(a in res$attribute) {
      entity[which(entity$idUser == id),paste(table,".",a,sep="")] <- res$value[which(res$attribute == a)]
    }
  }

  #users <<- entity
  return(entity)
}

###############################
# Merge action table with aav
###############################
buildActionsTable <- function(sourcedb=NULL) {
  table <- "action"
  eav <- "aav"

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
    sql <- paste("select * from ","aav","where entityId =",id)
    q <- dbSendQuery(con,sql)
    res <- fetch(q,n=-1)

    id <- as.numeric(id)

    for(a in res$attribute) {
      entity[which(entity$idAction == id),paste(table,".",a,sep="")] <- res$value[which(res$attribute == a)]
    }
  }

  if(!is.null(sourcedb)) {
    entity <- entity[which(entity$action.sourcedb == sourcedb),]
  }

  #actions <<- entity
  return(entity)
}

getUsers <- function() {
  return(users[,c("idUser","username")])
}
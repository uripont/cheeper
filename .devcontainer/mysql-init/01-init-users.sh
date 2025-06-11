#!/usr/bin/env bash
set -e

# make sure these are set:
: "${MYSQL_ROOT_PASSWORD:?Need to set MYSQL_ROOT_PASSWORD}"
: "${MYSQL_DATABASE:?Need to set MYSQL_DATABASE}"
: "${DB_SERVER_USER:?Need to set DB_SERVER_USER}"
: "${DB_SERVER_PASS:?Need to set DB_SERVER_PASS}"
: "${DB_DEV_USER:?Need to set DB_DEV_USER}"
: "${DB_DEV_PASS:?Need to set DB_DEV_PASS}"

# 1) Drop & re-create the database
mysql --user=root --password="$MYSQL_ROOT_PASSWORD" <<-EOSQL
  DROP DATABASE IF EXISTS \`${MYSQL_DATABASE}\`;
  CREATE DATABASE \`${MYSQL_DATABASE}\`;
EOSQL

# 2) Create two users & grants
mysql --user=root --password="$MYSQL_ROOT_PASSWORD" <<-EOSQL
  -- server user (minimal DML)
  CREATE USER IF NOT EXISTS '${DB_SERVER_USER}'@'%' 
    IDENTIFIED BY '${DB_SERVER_PASS}';
  GRANT SELECT,INSERT,UPDATE,DELETE
    ON \`${MYSQL_DATABASE}\`.* 
    TO '${DB_SERVER_USER}'@'%';

  -- dev user (full privileges)
  CREATE USER IF NOT EXISTS '${DB_DEV_USER}'@'%' 
    IDENTIFIED BY '${DB_DEV_PASS}';
  GRANT ALL PRIVILEGES
    ON \`${MYSQL_DATABASE}\`.* 
    TO '${DB_DEV_USER}'@'%';

  FLUSH PRIVILEGES;
EOSQL
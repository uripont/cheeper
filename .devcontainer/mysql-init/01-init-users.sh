#!/usr/bin/env bash
set -e

# Fail fast if any of these are missing
: "${MYSQL_ROOT_PASSWORD:?Need to set MYSQL_ROOT_PASSWORD}"
: "${MYSQL_DATABASE:?Need to set MYSQL_DATABASE}"
: "${DB_SERVER_USER:?Need to set DB_SERVER_USER}"
: "${DB_SERVER_PASS:?Need to set DB_SERVER_PASS}"
: "${DB_DEV_USER:?Need to set DB_DEV_USER}"
: "${DB_DEV_PASS:?Need to set DB_DEV_PASS}"

# Use the Unix socket (no TCP) to connect as root
mysql \
  --user=root \
  --password="$MYSQL_ROOT_PASSWORD" \
  --database="$MYSQL_DATABASE" <<-EOSQL
    -- 1) Create the “server” user (minimal privileges)
    CREATE USER IF NOT EXISTS '$DB_SERVER_USER'@'%'
      IDENTIFIED BY '$DB_SERVER_PASS';
    GRANT SELECT,INSERT,UPDATE,DELETE
      ON \`${MYSQL_DATABASE}\`.* 
      TO '$DB_SERVER_USER'@'%';

    -- 2) Create the “dev” user (full privileges)
    CREATE USER IF NOT EXISTS '$DB_DEV_USER'@'%'
      IDENTIFIED BY '$DB_DEV_PASS';
    GRANT ALL PRIVILEGES
      ON \`${MYSQL_DATABASE}\`.* 
      TO '$DB_DEV_USER'@'%';

    FLUSH PRIVILEGES;
EOSQL
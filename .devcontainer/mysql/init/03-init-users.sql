USE ${DB_NAME};

-- Create server user with appropriate privileges
CREATE USER IF NOT EXISTS '${DB_USER}'@'%' IDENTIFIED BY '${DB_PASS}';

-- Grant minimal required privileges for the application
GRANT SELECT, INSERT, UPDATE, DELETE ON ${DB_NAME}.* TO '${DB_USER}'@'%';

-- Additional specific privileges that might be needed
GRANT TRIGGER ON ${DB_NAME}.* TO '${DB_USER}'@'%';
GRANT EXECUTE ON ${DB_NAME}.* TO '${DB_USER}'@'%';

-- Apply privileges
FLUSH PRIVILEGES;

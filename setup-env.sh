#!/bin/bash

# Script to export environment variables from .env file to local environment
# Sets up environment vars when running Tomcat/MySQL locally without containers

ENV_FILE=$(dirname "$0")/.env

if [ ! -f "$ENV_FILE" ]; then
    echo "Error: .env file not found at $ENV_FILE"
    exit 1
fi

echo "Loading environment variables from $ENV_FILE..."

# Read each line from .env file
while IFS= read -r line || [[ -n "$line" ]]; do
    # Skip empty lines and comments
    if [ -z "$line" ] || [[ "$line" =~ ^# ]]; then
        continue
    fi
    
    # Remove any trailing comments
    line=$(echo "$line" | sed 's/#.*$//')
    
    # Trim whitespace
    line=$(echo "$line" | xargs)
    
    # Skip if line is empty after trimming
    if [ -z "$line" ]; then
        continue
    fi
    
    # Export the variable
    export "$line"
    echo "Exported: $line"
done < "$ENV_FILE"

echo "Environment variables loaded successfully."
echo "To use these variables in your current shell, run:"
echo "source $(basename "$0")"
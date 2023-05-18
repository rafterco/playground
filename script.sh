#!/bin/bash

# Check if file path parameter is provided
if [ $# -eq 0 ]; then
  echo "File path not provided."
  exit 1  # Failure
fi

file_path=$1

# Check if the file exists
if [ -f "$file_path" ]; then
  echo "File exists: $file_path"

  # Run another command before SFTP
  ls -l "$file_path"

  # Check the exit status of the previous command
  if [ $? -eq 0 ]; then
    echo "Command successful. Proceeding with SFTP."

    # Attempt to SFTP the file
    sftp user@hostname <<EOF
      put "$file_path"
      quit
EOF

    # Check the exit status of the previous command
    if [ $? -eq 0 ]; then
      echo "SFTP successful."
      exit 0  # Success
    else
      echo "SFTP failed."
      exit 1  # Failure
    fi

  else
    echo "Command failed. Cannot proceed with SFTP."
    exit 1  # Failure
  fi

else
  echo "File does not exist: $file_path"
  exit 1  # Failure
fi

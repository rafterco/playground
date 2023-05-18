#!/bin/bash

echo "Executing script.sh"

# Check if the file exists
if [ -f "file_to_transfer.txt" ]; then
  echo "File exists."

  # Attempt to SFTP the file
  sftp user@hostname <<EOF
    put file_to_transfer.txt
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
  echo "File does not exist."
  exit 1  # Failure
fi

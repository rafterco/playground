#!/bin/bash

echo "Executing script.sh"

# Simulating a success condition
if [ -f "existing_file.txt" ]; then
  echo "File exists."
  exit 0  # Success
else
  echo "File does not exist."
  exit 1  # Failure
fi

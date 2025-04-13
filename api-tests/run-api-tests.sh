#!/bin/bash

# Set up colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Running API tests for Order Processing System...${NC}"

# Already in the api-tests directory

# Check if node_modules exists, if not, install dependencies
if [ ! -d "node_modules" ]; then
  echo -e "${YELLOW}Installing dependencies...${NC}"
  npm install
fi

# Run the tests
echo -e "${YELLOW}Running tests...${NC}"
npm test

# Get the exit code
EXIT_CODE=$?

# Stay in the api-tests directory

# Exit with the same code as the tests
if [ $EXIT_CODE -eq 0 ]; then
  echo -e "${GREEN}All tests passed!${NC}"
else
  echo -e "${RED}Some tests failed. See above for details.${NC}"
fi

exit $EXIT_CODE

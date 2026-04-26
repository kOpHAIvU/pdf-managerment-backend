#!/bin/sh
set -eu

awslocal dynamodb create-table \
  --table-name users \
  --attribute-definitions \
    AttributeName=id,AttributeType=S \
    AttributeName=email,AttributeType=S \
  --key-schema \
    AttributeName=id,KeyType=HASH \
  --global-secondary-indexes \
    "[{\"IndexName\":\"email-index\",\"KeySchema\":[{\"AttributeName\":\"email\",\"KeyType\":\"HASH\"}],\"Projection\":{\"ProjectionType\":\"ALL\"}}]" \
  --billing-mode PAY_PER_REQUEST || true

awslocal dynamodb create-table \
  --table-name documents \
  --attribute-definitions \
    AttributeName=id,AttributeType=S \
    AttributeName=ownerUserId,AttributeType=S \
  --key-schema \
    AttributeName=id,KeyType=HASH \
  --global-secondary-indexes \
    "[{\"IndexName\":\"owner-index\",\"KeySchema\":[{\"AttributeName\":\"ownerUserId\",\"KeyType\":\"HASH\"}],\"Projection\":{\"ProjectionType\":\"ALL\"}}]" \
  --billing-mode PAY_PER_REQUEST || true

awslocal dynamodb create-table \
  --table-name document_acl \
  --attribute-definitions \
    AttributeName=documentId,AttributeType=S \
    AttributeName=principal,AttributeType=S \
  --key-schema \
    AttributeName=documentId,KeyType=HASH \
    AttributeName=principal,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST || true

awslocal dynamodb create-table \
  --table-name audit_events \
  --attribute-definitions \
    AttributeName=documentId,AttributeType=S \
    AttributeName=eventId,AttributeType=S \
  --key-schema \
    AttributeName=documentId,KeyType=HASH \
    AttributeName=eventId,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST || true

awslocal dynamodb create-table \
  --table-name document_share_links \
  --attribute-definitions \
    AttributeName=documentId,AttributeType=S \
    AttributeName=tokenHash,AttributeType=S \
  --key-schema \
    AttributeName=documentId,KeyType=HASH \
    AttributeName=tokenHash,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST || true

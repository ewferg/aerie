================
Activity Plans
================

Aerie provides an API to manage a database of plans. The database of plans may be queried for
a list of all plans, and new plans may be added to the repository. Existing plans may be
retrieved in full, replaced in full or in part, or deleted in full. The list of activities
in a plan may be appended to (by creating a new activity) and retrieved in full. Individual
activities in a plan may be retrieved in full, replaced in full or in part, and deleted in
full.

Operations on plans are validated to ensure consistency with the mission model-specific activity
model with which they are associated. Stored plans shall contain activities whose parameter
names and types are defined by the associated activity type.

.. toctree::
  mission-model-configuration-parameters
  precomputed-profiles
  simulation-configuration
  simulation-results
  scheduling-guide

# System Requirements

## Software Requirements

Name | Version
--- | ---
Docker | 19.X
PostgreSQL | 14.X

## Supported Browsers

Name | Version
--- | ---
Chrome | Latest
Firefox | Latest

## Hardware Requirements

Hardware | Details
--- | ---
CPU | 2 Gigahertz (GHZ) or above
RAM | 8 GB at minimum
Storage | 15 GB (the system should have access to additional storage as system databases grow according to mission operations) 
Display resolution | 2560-BY-1600, recommended
Internet connection | High-Speed connection, at least 10MBPS

### AWS EC2 Instance Type

The workload that can be submitted to Aerie is highly dependent on the computational complexity of the mission model being simulated. An [m4.large](https://aws.amazon.com/ec2/instance-types/) or greater EC2 instance will satisfy generic usage of Aerie with simple mission model. For missions that develop more complex mission models, operations such as performing simulation will benefit from the increased CPU of a [c4.xlarge](https://aws.amazon.com/ec2/instance-types/) or greater instance.

For the Aerie Commanding container a low cost / higher vCPU count instance for the service (~8ish is a good initial baseline). The key is that vCPU count directly relates to runtime as it allows more parallelization.

## TCP Port Requirements

| Service | Default Port | Public |
| --- | --- | --- |
| Aerie Commanding | 27184 | No |
| Aerie Gateway | 9000 | Yes |
| Aerie Merlin | 27183 | No |
| Aerie Merlin Worker | 27187 | No |
| Aerie Scheduler | 27185 | No |
| Aerie UI | 80 | Yes |
| Hasura | 8080 | Yes |
| Postgres | 5432 | No |

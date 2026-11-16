# E-Commerce DevOps Project

## Project Overview
This project demonstrates an end-to-end DevOps CI/CD pipeline for an E-Commerce application using Jenkins, GitHub, Maven, Docker, and AWS EC2.

## Technologies Used

- **Version Control:** Git, GitHub
- **CI/CD:** Jenkins
- **Build Tool:** Maven
- **Containerization:** Docker
- **Cloud Platform:** AWS EC2
- **Operating System:** Ubuntu Linux
- **Authentication:** SSH

## Project Architecture

Developer → GitHub → Jenkins → Maven Build → Docker Build → Docker Hub → AWS EC2 Deployment

## Project Structure

```text
ecommer-15-11-26/
├── src/
├── pom.xml
├── Dockerfile
├── Jenkinsfile
└── README.md
```

## CI/CD Pipeline Stages

1. **Source Code Management:** Developer pushes code to GitHub.
2. **Git Checkout:** Jenkins pulls the latest source code.
3. **Build:** Maven compiles the application.
4. **Docker Build:** Docker creates an application image.
5. **Docker Push:** Image is pushed to Docker Hub.
6. **Deployment:** Application is deployed on AWS EC2.

## SSH Configuration

Jenkins connects to GitHub using SSH authentication.

- SSH key authentication
- GitHub repository access
- Jenkins credentials management

## Project Objectives

- Automate application builds.
- Implement CI/CD using Jenkins.
- Containerize applications using Docker.
- Automate deployments on AWS EC2.
- Understand real-time DevOps workflows.

## Author

**Suresh Katta**

DevOps | AWS Cloud | Linux | Docker | Jenkins | Kubernetes | Terraform

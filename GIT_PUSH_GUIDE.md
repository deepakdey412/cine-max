# GitHub Push Guide

## Quick Commands

```bash
# Stage all changes
git add .

# Commit changes
git commit -m "Initial commit: Movie Booking Platform with Swagger API"

# Push to GitHub
git push origin main
```

## What Will Be Pushed

✅ Source code (backend + frontend)  
✅ Configuration files  
✅ README.md  
✅ Dockerfiles  
✅ Package files (pom.xml, package.json)  

## What Will NOT Be Pushed (Ignored)

❌ .gitignore (root level)  
❌ node_modules/  
❌ target/  
❌ .idea/, .vscode/  
❌ *.log files  
❌ .DS_Store, Thumbs.db  

## Before Pushing

**IMPORTANT:** Update sensitive data in `backend/src/main/resources/application.properties`:

```properties
# Change these before pushing:
spring.datasource.password=YOUR_PASSWORD
jwt.secret=YOUR_SECRET_KEY
app.admin.password=YOUR_ADMIN_PASSWORD
```

## First Time Setup

```bash
# Initialize git (if not done)
git init

# Add remote repository
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO.git

# Stage and commit
git add .
git commit -m "Initial commit"

# Push to GitHub
git push -u origin main
```

## After Cloning (For Others)

```bash
# Clone repository
git clone https://github.com/YOUR_USERNAME/YOUR_REPO.git
cd YOUR_REPO

# Install frontend dependencies
cd frontend
npm install

# Backend will download dependencies automatically when running
cd ../backend
mvn spring-boot:run
```

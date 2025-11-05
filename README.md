# Bugbusters - D&D Campaign Management System

## 📋 Project Overview

**Bugbusters** is a comprehensive web application for managing Dungeons & Dragons campaigns, developed as a final project for a Java course. The system provides a complete platform for players, dungeon masters, and administrators to organize D&D sessions, manage character sheets, and coordinate gameplay.

## 🏗️ Architecture

### Backend Stack

- **Framework**: Spring Boot 3.5.6
- **Java Version**: Java 21
- **Database**: MySQL with JPA/Hibernate
- **Security**: Spring Security with JWT authentication
- **Build Tool**: Maven
- **Additional Features**: Scheduled tasks enabled

### Frontend Stack

- **Core**: Vanilla JavaScript (ES6 Modules)
- **Styling**: CSS3 with custom components
- **UI Pattern**: Modular component-based architecture
- **API Communication**: RESTful APIs with JWT tokens

## 🎯 Core Features

### 1. User Management System

- **Multi-role Authentication**: Admin, Master (DM), Player roles
- **JWT Token Security**: Stateless authentication with configurable expiration
- **User Administration**: Ban/unban users, schedule account deletions
- **Profile Management**: User profiles with customizable images

### 2. Character Sheet Management

- **Character Creation**: Create D&D character sheets with classes, races, and levels
- **Sheet Editing**: Comprehensive character sheet editor
- **Character Progression**: Track character advancement and statistics

### 3. Campaign Management

- **Campaign Creation**: Masters can create and configure campaigns
- **Invite System**: Join campaigns via unique invite codes
- **Session Scheduling**: Organize and plan gaming sessions
- **Orphaned Campaigns**: Handle campaigns when masters leave

### 4. Session Proposal System

- **Proposal Creation**: Masters propose session dates and times
- **Voting Mechanism**: Players vote on proposed sessions
- **Automated Scheduling**: System determines optimal session times

### 5. Administrative Dashboard

- **User Oversight**: Monitor and manage all users
- **Campaign Administration**: Oversee all campaigns and sessions
- **System Statistics**: View platform usage and metrics

## 🗄️ Database Schema

### Core Entities

#### User Management

```java
@Entity User
- id: Long (Primary Key)
- username: String (Unique)
- email: String (Unique)
- passwordHash: String
- createdAt: LocalDateTime
- isBanned: boolean
- deletionScheduledOn: LocalDateTime
- profileImageUrl: String
```

#### Role System

```java
@Entity Role
- Relationships: ManyToMany with User
- Predefined roles: ADMIN, MASTER, PLAYER
```

#### Game Entities

```java
@Entity CharacterSheet
- Character information (name, class, race, level)
- Belongs to Player

@Entity Campaign
- Campaign details and settings
- Managed by Master
- Contains multiple sessions

@Entity CampaignSession
- Individual game sessions
- Links to proposals and participants

@Entity SessionProposal
- Proposed session dates
- Voting mechanism

@Entity ProposalVote
- Individual votes on proposals
```

## 🔧 Configuration

### Database Configuration

```properties
# MySQL Connection
spring.datasource.url=jdbc:mysql://localhost:3306/tavern_portal
spring.datasource.username=root
spring.datasource.password=root

# JPA/Hibernate Settings
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
```

### Security Configuration

```properties
# JWT Configuration
bugbusters.app.jwtSecret=BugbustersSecretKeyPerIlProgettoFinaleDiJavaMoltoLungaESicura12345!
bugbusters.app.jwtExpirationMs=28800000  # 8 hours
```

## 🎮 User Roles & Permissions

### Admin

- Full system access
- User management (ban/unban, delete accounts)
- Campaign oversight
- System administration

### Master (Dungeon Master)

- Create and manage campaigns
- Invite players to campaigns
- Propose session dates
- Manage campaign sessions
- Access master dashboard

### Player

- Create and manage character sheets
- Join campaigns via invite codes
- Vote on session proposals
- Participate in campaigns
- Access player dashboard

## 🌐 API Endpoints

### Authentication

```
POST /api/auth/login         # User login
POST /api/auth/register      # User registration
POST /api/auth/logout        # User logout
```

### Player Endpoints

```
GET  /api/player/sheets                    # Get player's character sheets
POST /api/player/sheets                    # Create new character sheet
POST /api/player/campaigns/join            # Join campaign with invite code
GET  /api/player/campaigns/joined          # Get joined campaigns
GET  /api/player/campaigns/orphaned        # Get orphaned campaigns
GET  /api/player/proposals                 # Get session proposals
POST /api/player/proposals/{id}/vote       # Vote on session proposal
```

### Master Endpoints

```
GET  /api/master/campaigns                 # Get master's campaigns
POST /api/master/campaigns                 # Create new campaign
POST /api/master/proposals                 # Create session proposal
GET  /api/master/campaigns/{id}/players    # Get campaign players
```

### Admin Endpoints

```
GET  /api/admin/users                      # Get all users
POST /api/admin/users/{id}/ban             # Ban user
POST /api/admin/users/{id}/unban           # Unban user
DELETE /api/admin/users/{id}               # Delete user
```

## 🖥️ Frontend Architecture

### Page Structure

```
landing.html          # Landing page and authentication
register.html         # User registration
profile.html          # Role selection and profile management
player.html           # Player dashboard
master.html           # Master dashboard
admin.html            # Admin dashboard
edit-sheet.html       # Character sheet editor
player-campaign-detail.html   # Campaign details for players
master-campaign-detail.html   # Campaign details for masters
```

### JavaScript Modules

```
modules/
├── api.js            # API communication utilities
├── auth.js           # Authentication and authorization
└── ui.js             # UI components and modals

page-controllers/
├── page.player.js        # Player dashboard logic
├── page.master.js        # Master dashboard logic
├── page.admin.js         # Admin dashboard logic
├── page.edit-sheet.js    # Character sheet editor
└── page.landing.js       # Landing page logic
```

### Authentication Flow

1. User authentication check on page load
2. Role-based redirects to appropriate dashboards
3. JWT token validation for API requests
4. Automatic logout on token expiration

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher
- Modern web browser

### Installation

1. **Clone the repository**

   ```bash
   git clone https://github.com/DelirioCasuale/Bugbusters.git
   cd Bugbusters
   ```

2. **Set up MySQL database**

   ```sql
   CREATE DATABASE tavern_portal;
   -- Run the database initialization scripts in src/main/resources/others/MySQL/
   ```

3. **Configure application properties**

   ```bash
   # Update src/main/resources/application.properties
   # Set your database credentials and JWT secret
   ```

4. **Build and run the application**

   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

5. **Access the application**
   ```
   http://localhost:8080
   ```

### Default Setup

- The application will start on port 8080
- Database tables are validated against entities (ddl-auto=validate)
- JWT tokens expire after 8 hours
- SQL queries are logged for debugging

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/generation/Bugbusters/
│   │   ├── controller/          # REST controllers
│   │   ├── entity/              # JPA entities
│   │   ├── service/             # Business logic
│   │   ├── repository/          # Data access layer
│   │   ├── dto/                 # Data transfer objects
│   │   ├── config/              # Configuration classes
│   │   └── BugbustersApplication.java
│   └── resources/
│       ├── static/              # Frontend assets
│       │   ├── css/             # Stylesheets
│       │   ├── js/              # JavaScript modules
│       │   ├── images/          # Images and icons
│       │   └── *.html           # HTML pages
│       ├── templates/           # Thymeleaf templates (if used)
│       ├── others/              # Database scripts and documentation
│       └── application.properties
└── test/                        # Test classes
```

## 🔄 Development Workflow

### Current Branch: `provaMergeFrontend`

The project is currently on a merge branch suggesting active frontend development and integration.

### Development Areas

- **DEV-UNDERGOING-MERGE/**: Contains work-in-progress features
- **TavernPortal_prototype/**: Prototype implementations and mockups

### Key Development Practices

- Modular JavaScript architecture
- RESTful API design
- Role-based security
- Responsive web design
- Clean code principles

## 🛡️ Security Features

### Authentication

- JWT-based stateless authentication
- Secure password hashing
- Token expiration handling
- Role-based access control

### Authorization

- Method-level security with `@PreAuthorize`
- Role-specific endpoints
- Protected frontend routes
- CSRF protection

### Data Protection

- Input validation
- SQL injection prevention
- XSS protection
- Secure session management

## 🎨 UI/UX Features

### Responsive Design

- Mobile-friendly interface
- Adaptive layouts
- Touch-friendly controls

### Interactive Elements

- Modal dialogs for forms
- Dynamic content loading
- Real-time updates
- Intuitive navigation

### Theming

- D&D-inspired design
- Consistent color scheme
- Custom fonts (Cinzel Decorative, Roboto)
- Font Awesome icons

## 📊 Future Enhancements

### Planned Features

- Real-time session management
- Enhanced character sheet customization
- Campaign statistics and analytics
- Mobile application
- Integration with D&D APIs

### Technical Improvements

- WebSocket integration for real-time features
- Enhanced caching mechanisms
- API rate limiting
- Comprehensive test coverage

## 🤝 Contributing

This project was developed as a final Java course project. The codebase demonstrates:

- Modern Spring Boot practices
- Clean architecture principles
- Full-stack development skills
- Database design and management
- Frontend JavaScript development

## 📄 License

This project is developed for educational purposes as part of a Java programming course.

---

**Project Team**: Bugbusters Development Team  
**Institution**: Generation Italy - Java Development Course  
**Language**: Italian (Interface and Documentation)  
**Status**: Active Development

#!/usr/bin/env node

// Load and test the SpringSecurityValidator
// Copy the class from the main file (simplified for Node.js)

class SpringSecurityValidator {
  constructor() {
    this.users = [
      {
        username: 'admin',
        password: 'admin',
        role: 'ADMIN',
        authorities: ['ROLE_ADMIN'],
        master: {},
        player: {},
      },
      {
        username: 'guest1',
        password: 'password1',
        role: 'GUEST',
        authorities: ['ROLE_GUEST'],
        master: {},
        player: {
          characters: [],
        },
      },
      {
        username: 'guest2',
        password: 'password2',
        role: 'GUEST',
        authorities: ['ROLE_GUEST'],
        master: {
          campaigns: [],
        },
        player: {},
      },
      {
        username: 'guest3',
        password: 'password3',
        role: 'GUEST',
        authorities: ['ROLE_GUEST'],
        master: {
          campaigns: [],
        },
        player: {
          characters: [],
        },
      },
    ];
    this.currentUser = null;
  }

  authenticate(username, password) {
    const user = this.users.find(
      (u) => u.username === username && u.password === password
    );

    if (user) {
      this.currentUser = {
        username: user.username,
        role: user.role,
        authorities: user.authorities,
        authenticated: true,
      };

      return {
        success: true,
        user: this.currentUser,
      };
    }

    return {
      success: false,
      error: 'Invalid credentials',
    };
  }

  isAuthenticated() {
    return this.currentUser && this.currentUser.authenticated;
  }

  hasRole(role) {
    if (!this.isAuthenticated()) return false;
    return this.currentUser.authorities.includes(`ROLE_${role}`);
  }

  hasAuthority(authority) {
    if (!this.isAuthenticated()) return false;
    return this.currentUser.authorities.includes(authority);
  }

  isMaster() {
    if (!this.isAuthenticated()) return false;
    const fullUser = this.users.find(
      (u) => u.username === this.currentUser.username
    );
    return (
      fullUser && fullUser.master && Object.keys(fullUser.master).length > 0
    );
  }

  isPlayer() {
    if (!this.isAuthenticated()) return false;
    const fullUser = this.users.find(
      (u) => u.username === this.currentUser.username
    );
    return (
      fullUser && fullUser.player && Object.keys(fullUser.player).length > 0
    );
  }

  getCurrentUser() {
    return this.currentUser;
  }

  logout() {
    this.currentUser = null;
  }

  login(username, password) {
    const result = this.authenticate(username, password);
    return result.success;
  }
}

// Create global instance
global.validator = new SpringSecurityValidator();

// Helper functions
global.login = (username, password) => {
  const result = global.validator.login(username, password);
  console.log(result ? '✅ Login successful' : '❌ Login failed');
  return result;
};

global.logout = () => {
  global.validator.logout();
  console.log('🔓 Logged out');
};

global.status = () => {
  console.log('--- Authentication Status ---');
  console.log('Authenticated:', global.validator.isAuthenticated());
  console.log('Current User:', global.validator.getCurrentUser());
  if (global.validator.isAuthenticated()) {
    console.log('Has ADMIN role:', global.validator.hasRole('ADMIN'));
    console.log('Has GUEST role:', global.validator.hasRole('GUEST'));
    console.log('Is Master:', global.validator.isMaster());
    console.log('Is Player:', global.validator.isPlayer());
  }
};

global.users = () => {
  console.log('--- Available Test Users ---');
  global.validator.users.forEach((user) => {
    console.log(
      `Username: ${user.username}, Password: ${user.password}, Role: ${user.role}`
    );
  });
};

// Welcome message
console.log('🔐 SpringSecurityValidator Test Environment');
console.log('');
console.log('Available commands:');
console.log('  login("username", "password") - Login with credentials');
console.log('  logout() - Logout current user');
console.log('  status() - Show current authentication status');
console.log('  users() - Show available test users');
console.log('  validator.methodName() - Call any validator method directly');
console.log('');
console.log('Example: login("admin", "admin")');
console.log('');

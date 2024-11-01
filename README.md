The Book Network Backend provides a robust API for managing a social network centered around books. This backend allows users to create profiles, follow others, review books, and interact over shared reading interests. Built with Node.js and Express, it integrates with MongoDB to handle data storage for users, books, and interactions.

Table of Contents
Features
Tech Stack
Installation
Environment Variables
Usage
API Endpoints
Contributing
License
Features
User Authentication: Secure registration and login using JWT.
Book Management: CRUD operations to manage a catalog of books.
User Profiles: Profiles with preferences and book interactions.
Social Features: Follow/unfollow users and view recommendations.
Review System: Users can review, rate, and comment on books.
Search: Search books by title, author, or genre.
Tech Stack
Node.js and Express.js: Backend framework and routing.
MongoDB: Database for persisting user and book data.
Mongoose: ODM for MongoDB data modeling.
JWT: For token-based user authentication.
Installation
Clone the repository:

bash
Copy code
git clone https://github.com/Dhiraj-231/book-network-backend.git
cd book-network-backend
Install dependencies:

bash
Copy code
npm install
Set up environment variables (see Environment Variables below).

Start the server:

bash
Copy code
npm start
Environment Variables
Create a .env file in the root directory and add the following variables:

plaintext
Copy code
PORT=3000
MONGO_URI=your_mongo_connection_string
JWT_SECRET=your_jwt_secret
Usage
After completing installation, you can use Postman or any API client to test the endpoints locally on http://localhost:3000.

API Endpoints
Authentication
POST /api/auth/register: Register a new user.
POST /api/auth/login: Log in a user and receive a JWT.
Users
GET /api/users/:id: Retrieve a user profile.
POST /api/users/follow/:id: Follow a user.
POST /api/users/unfollow/:id: Unfollow a user.
Books
GET /api/books: List all books.
POST /api/books: Add a new book (Admin only).
PUT /api/books/:id: Update a book (Admin only).
DELETE /api/books/:id: Delete a book (Admin only).
Reviews
POST /api/reviews/:bookId: Add a review for a book.
GET /api/reviews/:bookId: List all reviews for a book.
Contributing
Contributions are welcome! Please follow these steps:

Fork the repository.
Create a new branch for your feature (git checkout -b feature-name).
Commit your changes (git commit -m 'Add some feature').
Push to the branch (git push origin feature-name).
Open a Pull Request.
License
This project is licensed under the MIT License.

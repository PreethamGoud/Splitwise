# Splitwise Frontend

Angular 19 + Angular Material frontend for the Splitwise backend. Production-quality UI with authentication, groups, expenses, and settlements.

## Features

- **Auth**: Login, signup, JWT storage, auth guard
- **Dashboard**: Current user, balances across all groups, list of groups, create new group
- **Group detail**: Members, balances, add member, add expense (EQUAL / EXACT / PERCENTAGE split), suggested settlements, record settlement

## Prerequisites

- Node.js 18+
- Backend running at `http://localhost:9090` (or set `apiUrl` in `src/environments/environment.ts`)

## Development

```bash
npm install
ng serve
```

Open http://localhost:4200. Use **Login** or **Create account** to sign in. The app uses the backend at `http://localhost:9090` by default (see `src/environments/environment.ts`).

## Build

```bash
ng build
```

Output in `dist/frontend`. For production, configure `src/environments/environment.prod.ts` with your API URL and build with `ng build --configuration=production`.

## Backend CORS

The backend has been configured with CORS so the frontend can call it from another origin. Ensure the backend is running when using the app.

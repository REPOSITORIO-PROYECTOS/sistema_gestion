import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";

const protectedRoutes = ["/", "/admin", "/estudiante"];

export function middleware(request: NextRequest) {
    const { pathname } = request.nextUrl;

    const role = request.cookies.get("role")?.value;

    const isProtectedRoute = protectedRoutes.some((route) =>
        pathname.startsWith(route)
    );
    console.log("Middleware activo - pathname:", pathname);
    console.log("Role cookie:", role);
    if (isProtectedRoute && role !== "ROLE_ADMIN") {
        const loginUrl = new URL("/login", request.url);
        loginUrl.searchParams.set("from", pathname);
        return NextResponse.redirect(loginUrl);
    }

    if (pathname === "/login" && role === "ROLE_ADMIN") {
        return NextResponse.redirect(new URL("/", request.url));
    }

    return NextResponse.next();
}

export const config = {
    matcher: [
      "/((?!api|_next|_vercel|.*\\..*).*)",
    ],
  };
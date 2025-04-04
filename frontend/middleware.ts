// import { NextResponse } from "next/server";
// import type { NextRequest } from "next/server";

// // Rutas que requieren autenticación
// const protectedRoutes = ["/admin", "/estudiante"];
// // Rutas públicas
// const publicRoutes = ["/", "/login"];

// export function middleware(request: NextRequest) {
//     const { pathname } = request.nextUrl;

//     // Verificar si hay un token en las cookies
//     const authToken = request.cookies.get("auth-storage")?.value;
//     const isAuthenticated = authToken
//         ? JSON.parse(authToken).state.isAuthenticated
//         : false;

//     // Verificar si la ruta actual requiere autenticación
//     const isProtectedRoute = protectedRoutes.some((route) =>
//         pathname.startsWith(route)
//     );

//     // Si es una ruta protegida y no está autenticado, redirigir al login
//     if (isProtectedRoute && !isAuthenticated) {
//         const url = new URL("/login", request.url);
//         url.searchParams.set("from", pathname);
//         return NextResponse.redirect(url);
//     }

//     // Si está autenticado y va al login, redirigir a la página principal
//     if (pathname === "/login" && isAuthenticated) {
//         return NextResponse.redirect(new URL("/", request.url));
//     }

//     return NextResponse.next();
// }

// // Configurar las rutas en las que se ejecutará el middleware
// export const config = {
//     matcher: [
//         /*
//          * Coincide con todas las rutas excepto:
//          * 1. /api (rutas API)
//          * 2. /_next (archivos de Next.js)
//          * 3. /_vercel (archivos de Vercel)
//          * 4. /favicon.ico, /sitemap.xml, etc.
//          */
//         "/((?!api|_next|_vercel|.*\\..*).*)",
//     ],
// };

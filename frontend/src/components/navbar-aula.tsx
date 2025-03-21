"use client";

import Link from "next/link";
import React from "react";
import { Button } from "./ui/button";
import { useAuth } from "./providers/auth-provider";
import { usePathname } from "next/navigation";
import { ModeToggle } from "./mode-toggle";
import { Navbar } from "./navbar";

export default function NavbarAula() {
    const pathname = usePathname();
    const { logout } = useAuth();
    if (pathname === "/login") {
        return null;
    }
    if (pathname === "/admin") {
        return <Navbar />;
    }
    return (
        <header className="border-b shadow-md">
            <div className="container mx-auto flex h-16 items-center px-4">
                <div className="font-bold text-xl">Aula Virtual</div>
                <nav className="ml-auto flex items-center gap-4">
                    <Link
                        href="/"
                        className="text-sm font-medium hover:underline"
                    >
                        Inicio
                    </Link>
                    <Link
                        href="#"
                        className="text-sm font-medium hover:underline"
                    >
                        Cursos
                    </Link>
                    <Link
                        href="#"
                        className="text-sm font-medium hover:underline"
                    >
                        Mi Perfil
                    </Link>
                    <Button
                        variant="ghost"
                        onClick={logout}
                        className="text-sm font-semibold hover:bg-red-500  hover:text-white"
                    >
                        Cerrar sesión
                    </Button>
                    <ModeToggle />
                </nav>
            </div>
        </header>
    );
}

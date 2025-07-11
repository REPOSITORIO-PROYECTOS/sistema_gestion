"use client";

import Link from "next/link";
import React from "react";
import { Button } from "./ui/button";
import { useAuth } from "./providers/auth-provider";
import { usePathname } from "next/navigation";
import { ModeToggle } from "./mode-toggle";
import { Navbar } from "./navbar";
import Image from "next/image";

export default function NavbarAula() {
    const pathname = usePathname();
    const { logout } = useAuth();
    if (pathname === "/login") {
        return null;
    }
    if (pathname.startsWith("/admin")) {
        return <Navbar />;
    }

    return (
        <header className="relative z-50 border-b shadow-md bg-zinc-50 dark:bg-zinc-800 dark:border-gray-700">
            <div>
                <div className="bg-blue-800">
                                <Image
                                src={"/resources/isp.png"}
                                className="mt-3 ml-3 py-2"
                                width={50}
                                height={100}
                                alt="Logo"
                                />
                            </div>
            </div>
            <div className="container mx-auto flex h-12 items-center px-4">
                <div className="font-bold text-xl">Aula Virtual</div>
                <nav className="ml-auto flex items-center gap-4">
                    <Link
                        href="/"
                        className="text-sm font-medium hover:underline"
                    >
                        Inicio
                    </Link>
                    {/* <Link
                        href="#"
                        className="text-sm font-medium hover:underline"
                    >
                        Cursos
                    </Link> */}
                    {/* <Button
                        variant={"ghost"}
                        onClick={handleOpenProfile}
                        className="text-sm font-medium hover:underline"
                    >
                        Mi Perfil
                    </Button> */}
                    <Button
                        variant="ghost"
                        onClick={logout}
                        className="text-sm font-semibold hover:bg-red-500 hover:text-white"
                    >
                        Cerrar sesión
                    </Button>
                    <ModeToggle />
                </nav>
            </div>
        </header>
    );
}

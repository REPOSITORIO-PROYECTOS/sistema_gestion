"use client";

import { BookOpenIcon, PhoneCall } from "lucide-react";
import { useAuth } from "./providers/auth-provider";
import { useEffect, useRef, useState } from "react";
import { Button } from "@/components/ui/button";
import { usePathname } from "next/navigation";
import { ModeToggle } from "./mode-toggle";
import Link from "next/link";
import { useAuthStore } from "@/context/store";
import Image from "next/image";
import UserFormNoAdmin from "./form-user-no-admin";

export function Navbar() {
    const [profileOpen, setProfileOpen] = useState(false);
    const pathname = usePathname();
    const { user } = useAuthStore();
    const { logout } = useAuth();
    const [activeIndex, setActiveIndex] = useState(0);
    const [indicatorStyle, setIndicatorStyle] = useState({});
    const navRef = useRef<HTMLDivElement>(null);
    // const navItems = [
    //     { href: "/admin", label: "Home" },
    //     { href: "/admin/caja", label: "Caja" },
    //     { href: "/admin/cursos", label: "Cursos" },
    //     { href: "/admin/usuarios", label: "Usuarios" },
    //     { href: "/admin/cuotas", label: "Cuotas" },
    //     { href: "/admin/aula-virtual", label: "Aula Virtual" },
    // ];

    let navItems = [];

    (user?.role.includes("ROLE_ADMIN_USERS") || user?.role.includes("ROLE_ADMIN")) && 
    navItems.push({ href: "/admin", label: "Home" });

    (user?.role.includes("ROLE_CASHER") || user?.role.includes("ROLE_ADMIN")) &&
    navItems.push({ href: "/admin/caja", label: "Caja" });

    (user?.role.includes("ROLE_ADMIN_COURSES") || user?.role.includes("ROLE_ADMIN")) &&
    navItems.push({ href: "/admin/cursos", label: "Cursos" });

    (user?.role.includes("ROLE_ADMIN_USERS") || user?.role.includes("ROLE_ADMIN")) &&
    navItems.push({ href: "/admin/usuarios", label: "Usuarios" });

    (user?.role.includes("ROLE_CASHER") || user?.role.includes("ROLE_ADMIN")) &&
    navItems.push({ href: "/admin/cuotas", label: "Cuotas" });

    (user?.role.includes("ROLE_ADMIN_VC") || user?.role.includes("ROLE_ADMIN")) &&
    navItems.push({ href: "/admin/aula-virtual", label: "Aula Virtual" });

    user?.role.includes("ROLE_PARENT") &&
    navItems.push({ href: "/padres", label: "Padre" });

    useEffect(() => {
        const newActiveIndex = navItems.findIndex(
            (item) => item.href === pathname
        );
        setActiveIndex(newActiveIndex);
    }, [pathname]);

    useEffect(() => {
        if (navRef.current) {
            const activeButton = navRef.current.children[
                activeIndex
            ] as HTMLElement;
            if (activeButton) {
                setIndicatorStyle({
                    width: `${activeButton.offsetWidth}px`,
                    transform: `translateX(${activeButton.offsetLeft}px)`,
                });
            }
        }
    }, [activeIndex]);

    const handleOpenProfile = () => {
        setProfileOpen((profileOpen) => !profileOpen);
    };

    return (
        <nav className="relative flex flex-col z-50 shadow-md border-b dark:border-gray-700">
            <div className="bg-blue-800 mb-2">
                <Image
                    src={"/resources/isp.png"}
                    className="mt-2 ml-4 pb-2"
                    width={50}
                    height={100}
                    alt="Logo"
                />
            </div>
            <div className="container mx-auto px-6 pb-3 flex justify-between items-center space-x-4">
                <div className="space-x-2 relative" ref={navRef}>
                    {navItems.map((item, index) => (
                        <Button
                            key={item.href}
                            variant="ghost"
                            className={`
                                relative z-10 transition-colors duration-200
                                ${
                                    pathname === item.href
                                        ? "text-white hover:text-white hover:bg-transparent"
                                        : "hover:text-black "
                                }
                            `}
                            asChild
                        >
                            <Link href={item.href}>{item.label}</Link>
                        </Button>
                    ))}
                    <div
                        className="absolute bottom-0 -left-0 h-full bg-blue-700 transition-all duration-300 ease-in-out z-0 rounded-md"
                        style={indicatorStyle}
                    />
                </div>
                <div className="space-x-4 flex items-center">
                    <Button className="bg-blue-600 text-white hover:bg-blue-800">
                        Manual del Usuario
                        <BookOpenIcon
                            className="w-5 h-5 ml-2"
                            aria-hidden="true"
                        />
                    </Button>
                    <Button className="bg-blue-600 text-white hover:bg-blue-800">
                        Soporte
                        <PhoneCall
                            className="w-5 h-5 ml-2"
                            aria-hidden="true"
                        />
                    </Button>
                    <Button
                        variant={"ghost"}
                        onClick={handleOpenProfile}
                        className="text-sm font-medium hover:underline"
                    >
                        Mi Perfil
                    </Button>
                    <Button
                        variant="ghost"
                        onClick={logout}
                        className="text-sm font-semibold hover:bg-red-500 hover:text-white"
                    >
                        Cerrar sesión
                    </Button>
                    <ModeToggle />
                </div>
            </div>
            {profileOpen && (
                <UserFormNoAdmin
                    isEditable={true}
                    mutate={() => {}}
                    onClose={setProfileOpen}
                />
            )}
        </nav>
    );
}

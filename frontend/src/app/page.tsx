"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import {
    Card,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import { useAuth } from "@/components/providers/auth-provider";

export default function Home() {
    const { user, logout, isLoading } = useAuth();
    const [isClient, setIsClient] = useState(false);

    useEffect(() => {
        setIsClient(true);
    }, []);

    const course = {
        id: "67a656e0f9ff8d5d561d9a6e",
        title: "curso 2025",
        description: "guardado como usuario.",
        status: "ACTIVE",
        monthlyPrice: 7000.99,
        teacherId: "67a65539fff1b421994b0b33",
    };

    if (!isClient || isLoading) {
        return (
            <div className="flex h-screen items-center justify-center">
                Cargando...
            </div>
        );
    }

    return (
        <section className="min-h-screen">
            <div className="container mx-auto py-10">
                <div className="flex flex-col items-center justify-center space-y-4 text-center mb-10">
                    <h1 className="text-4xl font-bold tracking-tight">
                        Bienvenido, {user?.name}
                    </h1>
                    <p className="text-muted-foreground max-w-[700px]">
                        Accede a tus cursos y contenidos educativos desde
                        nuestra plataforma.
                    </p>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    <Card className="flex flex-col h-full">
                        <CardHeader>
                            <CardTitle>{course.title}</CardTitle>
                            <CardDescription>
                                {course.description}
                            </CardDescription>
                        </CardHeader>
                        <CardContent className="flex-grow">
                            <div className="space-y-2">
                                <div className="flex items-center justify-between">
                                    <span className="text-sm font-medium">
                                        Estado:
                                    </span>
                                    <span className="inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold bg-green-100 text-green-800 border-green-200">
                                        {course.status}
                                    </span>
                                </div>
                                <div className="flex items-center justify-between">
                                    <span className="text-sm font-medium">
                                        Precio mensual:
                                    </span>
                                    <span className="font-bold">
                                        $
                                        {course.monthlyPrice.toLocaleString(
                                            "es-ES"
                                        )}
                                    </span>
                                </div>
                            </div>
                        </CardContent>
                        <CardFooter>
                            <Link
                                href={`/aula-virtual/${course.id}`}
                                className="w-full"
                            >
                                <Button className="w-full">Ver curso</Button>
                            </Link>
                        </CardFooter>
                    </Card>

                    {/* Placeholder cards for more courses */}
                    <Card className="flex flex-col h-full">
                        <CardHeader>
                            <CardTitle>Programación Web</CardTitle>
                            <CardDescription>
                                Aprende a desarrollar aplicaciones web modernas.
                            </CardDescription>
                        </CardHeader>
                        <CardContent className="flex-grow">
                            <div className="space-y-2">
                                <div className="flex items-center justify-between">
                                    <span className="text-sm font-medium">
                                        Estado:
                                    </span>
                                    <span className="inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold bg-green-100 text-green-800 border-green-200">
                                        ACTIVE
                                    </span>
                                </div>
                                <div className="flex items-center justify-between">
                                    <span className="text-sm font-medium">
                                        Precio mensual:
                                    </span>
                                    <span className="font-bold">$5000.00</span>
                                </div>
                            </div>
                        </CardContent>
                        <CardFooter>
                            <Button className="w-full" variant="outline">
                                Ver curso
                            </Button>
                        </CardFooter>
                    </Card>

                    <Card className="flex flex-col h-full">
                        <CardHeader>
                            <CardTitle>Diseño UX/UI</CardTitle>
                            <CardDescription>
                                Fundamentos de diseño de experiencia de usuario.
                            </CardDescription>
                        </CardHeader>
                        <CardContent className="flex-grow">
                            <div className="space-y-2">
                                <div className="flex items-center justify-between">
                                    <span className="text-sm font-medium">
                                        Estado:
                                    </span>
                                    <span className="inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold bg-yellow-100 text-yellow-800 border-yellow-200">
                                        PRÓXIMAMENTE
                                    </span>
                                </div>
                                <div className="flex items-center justify-between">
                                    <span className="text-sm font-medium">
                                        Precio mensual:
                                    </span>
                                    <span className="font-bold">$6500.00</span>
                                </div>
                            </div>
                        </CardContent>
                        <CardFooter>
                            <Button
                                className="w-full"
                                variant="outline"
                                disabled
                            >
                                Próximamente
                            </Button>
                        </CardFooter>
                    </Card>
                </div>
            </div>

            <footer className="border-t py-6">
                <div className="container mx-auto px-4 text-center text-sm text-muted-foreground">
                    © {new Date().getFullYear()} Aula Virtual. Todos los
                    derechos reservados.
                </div>
            </footer>
        </section>
    );
}

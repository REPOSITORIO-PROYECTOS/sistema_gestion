"use client";

import type React from "react";
import { useState } from "react";
import { useRouter } from "next/navigation";
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
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";

export default function LoginPage() {
    const router = useRouter();
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const handleLogin = (e: React.FormEvent, role: "estudiante" | "admin") => {
        e.preventDefault();
        setLoading(true);
        setError("");

        // Simulación de autenticación
        setTimeout(() => {
            setLoading(false);

            // Credenciales de prueba
            if (
                role === "admin" &&
                email === "admin@example.com" &&
                password === "admin123"
            ) {
                // Guardar información de sesión (en una aplicación real usaríamos cookies o localStorage)
                sessionStorage.setItem(
                    "user",
                    JSON.stringify({
                        email,
                        role: "admin",
                        name: "Administrador",
                    })
                );
                router.push("/admin");
            } else if (
                role === "estudiante" &&
                email === "estudiante@example.com" &&
                password === "estudiante123"
            ) {
                // Guardar información de sesión
                sessionStorage.setItem(
                    "user",
                    JSON.stringify({
                        email,
                        role: "estudiante",
                        name: "Estudiante Demo",
                    })
                );
                router.push("/");
            } else {
                setError(
                    "Credenciales incorrectas. Por favor, inténtalo de nuevo."
                );
            }
        }, 1000);
    };

    return (
        <section className="w-full relative">
            <div className="absolute inset-0 -z-10 h-full w-full [background:radial-gradient(125%_125%_at_50%_10%,#ffffff00_40%,#3b82f6_100%)]"></div>
            <div className="container mx-auto flex h-screen w-full flex-col items-center justify-center">
                <div className="mx-auto flex w-full flex-col justify-center space-y-6 sm:w-[350px]">
                    <div className="flex flex-col space-y-2 text-center">
                        <h1 className="text-2xl font-semibold tracking-tight">
                            Aula Virtual
                        </h1>
                        <p className="text-sm text-muted-foreground">
                            Ingresa tus credenciales para acceder
                        </p>
                    </div>

                    <Tabs defaultValue="estudiante" className="w-full">
                        <TabsList className="grid w-full grid-cols-2 dark:bg-zinc-900">
                            <TabsTrigger value="estudiante">
                                Estudiante
                            </TabsTrigger>
                            <TabsTrigger value="admin">
                                Administrador
                            </TabsTrigger>
                        </TabsList>

                        <TabsContent value="estudiante">
                            <Card>
                                <CardHeader>
                                    <CardTitle>Acceso Estudiantes</CardTitle>
                                    <CardDescription className="h-12">
                                        Ingresa con tu correo y contraseña de
                                        estudiante
                                    </CardDescription>
                                </CardHeader>
                                <form
                                    onSubmit={(e) =>
                                        handleLogin(e, "estudiante")
                                    }
                                >
                                    <CardContent className="space-y-4">
                                        {error && (
                                            <div className="rounded-md bg-red-50 p-3 text-sm text-red-500">
                                                {error}
                                            </div>
                                        )}
                                        <div className="space-y-2">
                                            <Label htmlFor="email-estudiante">
                                                Correo electrónico
                                            </Label>
                                            <Input
                                                id="email-estudiante"
                                                type="email"
                                                placeholder="tu@email.com"
                                                value={email}
                                                onChange={(e) =>
                                                    setEmail(e.target.value)
                                                }
                                                required
                                            />
                                        </div>
                                        <div className="space-y-2">
                                            <div className="flex items-center justify-between">
                                                <Label htmlFor="password-estudiante">
                                                    Contraseña
                                                </Label>
                                                <Link
                                                    href="#"
                                                    className="text-xs text-blue-500 hover:underline"
                                                >
                                                    ¿Olvidaste tu contraseña?
                                                </Link>
                                            </div>
                                            <Input
                                                id="password-estudiante"
                                                type="password"
                                                value={password}
                                                onChange={(e) =>
                                                    setPassword(e.target.value)
                                                }
                                                required
                                            />
                                        </div>
                                    </CardContent>
                                    <CardFooter>
                                        <Button
                                            type="submit"
                                            className="w-full mt-6"
                                            disabled={loading}
                                        >
                                            {loading
                                                ? "Iniciando sesión..."
                                                : "Iniciar sesión"}
                                        </Button>
                                    </CardFooter>
                                </form>
                            </Card>
                        </TabsContent>

                        <TabsContent value="admin">
                            <Card>
                                <CardHeader>
                                    <CardTitle>
                                        Acceso Administradores
                                    </CardTitle>
                                    <CardDescription className="h-12">
                                        Ingresa con tus credenciales de
                                        administrador
                                    </CardDescription>
                                </CardHeader>
                                <form onSubmit={(e) => handleLogin(e, "admin")}>
                                    <CardContent className="space-y-4">
                                        {error && (
                                            <div className="rounded-md bg-red-50 p-3 text-sm text-red-500">
                                                {error}
                                            </div>
                                        )}
                                        <div className="space-y-2">
                                            <Label htmlFor="email-admin">
                                                Correo electrónico
                                            </Label>
                                            <Input
                                                id="email-admin"
                                                type="email"
                                                placeholder="admin@email.com"
                                                value={email}
                                                onChange={(e) =>
                                                    setEmail(e.target.value)
                                                }
                                                required
                                            />
                                        </div>
                                        <div className="space-y-2">
                                            <div className="flex items-center justify-between">
                                                <Label htmlFor="password-admin">
                                                    Contraseña
                                                </Label>
                                                <Link
                                                    href="#"
                                                    className="text-xs text-blue-500 hover:underline"
                                                >
                                                    ¿Olvidaste tu contraseña?
                                                </Link>
                                            </div>
                                            <Input
                                                id="password-admin"
                                                type="password"
                                                value={password}
                                                onChange={(e) =>
                                                    setPassword(e.target.value)
                                                }
                                                required
                                            />
                                        </div>
                                    </CardContent>
                                    <CardFooter>
                                        <Button
                                            type="submit"
                                            className="w-full mt-6"
                                            disabled={loading}
                                        >
                                            {loading
                                                ? "Iniciando sesión..."
                                                : "Iniciar sesión"}
                                        </Button>
                                    </CardFooter>
                                </form>
                            </Card>
                        </TabsContent>
                    </Tabs>

                    <p className="px-8 text-center text-sm text-muted-foreground">
                        ¿No tienes una cuenta?{" "}
                        <Link
                            href="#"
                            className="underline underline-offset-4 hover:text-primary"
                        >
                            Contacta con tu institución
                        </Link>
                    </p>
                </div>
            </div>
        </section>
    );
}

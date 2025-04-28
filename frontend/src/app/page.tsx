"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import {
    CalendarDays,
    Clock,
    BookOpen,
    Bell,
    ChevronRight,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import {
    Card,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { useAuth } from "@/components/providers/auth-provider";
import { useAuthStore } from "@/context/store";
import { useRouter } from "next/navigation";

type Course = {
    id: string;
    title: string;
    description: string;
    status: "ACTIVE" | "PENDING" | "COMPLETED";
    progress: number;
    nextClass?: string;
};

type News = {
    id: string;
    title: string;
    content: string;
    date: string;
    category: string;
    important?: boolean;
};

export default function Home() {
    //const { user, logout, isLoading } = useAuth();
    const {user} = useAuthStore();
    const router = useRouter();
    const [isClient, setIsClient] = useState(false);
    const [currentTime, setCurrentTime] = useState(new Date());

    // Datos de ejemplo - cursos inscritos
    const enrolledCourses: Course[] = [
        {
            id: "67a656e0f9ff8d5d561d9a6e",
            title: "Curso 2025",
            description: "Guardado como usuario",
            status: "ACTIVE",
            progress: 35,
            nextClass: "Lunes 25 de Marzo, 18:00hs",
        },
        {
            id: "67a656e0f9ff8d5d561d9a6f",
            title: "Programación Web",
            description: "Desarrollo de aplicaciones web modernas",
            status: "ACTIVE",
            progress: 68,
            nextClass: "Miércoles 27 de Marzo, 19:30hs",
        },
        {
            id: "67a656e0f9ff8d5d561d9a70",
            title: "Diseño UX/UI",
            description: "Fundamentos de diseño de experiencia de usuario",
            status: "PENDING",
            progress: 0,
        },
    ];

    // Datos de ejemplo - noticias
    const news: News[] = [
        {
            id: "1",
            title: "Nuevos cursos disponibles",
            content:
                "Hemos agregado 5 nuevos cursos a nuestra plataforma. ¡Explora las nuevas opciones en programación, diseño y marketing digital!",
            date: "22 de Marzo, 2025",
            category: "Plataforma",
            important: true,
        },
        {
            id: "2",
            title: "Mantenimiento programado",
            content:
                "La plataforma estará en mantenimiento el próximo domingo de 2:00 a 5:00 AM. Durante este período, algunos servicios podrían no estar disponibles.",
            date: "20 de Marzo, 2025",
            category: "Técnico",
        },
        {
            id: "3",
            title: "Webinar: Tendencias Educativas 2025",
            content:
                "No te pierdas nuestro próximo webinar sobre las tendencias educativas para este año. Inscríbete ahora para reservar tu lugar.",
            date: "18 de Marzo, 2025",
            category: "Eventos",
        },
    ];

    useEffect(() => {
        setIsClient(true);
        const timer = setInterval(() => {
            setCurrentTime(new Date());
        }, 60000);

        return () => clearInterval(timer);
    }, []);

    // if (!isClient || isLoading) {
    //     return (
    //         <div className="flex h-screen items-center justify-center">
    //             Cargando...
    //         </div>
    //     );
    // }

    // Formatear fecha y hora
    const formattedDate = currentTime.toLocaleDateString("es-ES", {
        weekday: "long",
        year: "numeric",
        month: "long",
        day: "numeric",
    });

    const formattedTime = currentTime.toLocaleTimeString("es-ES", {
        hour: "2-digit",
        minute: "2-digit",
    });

    // Capitalizar primera letra de la fecha
    const capitalizedDate =
        formattedDate.charAt(0).toUpperCase() + formattedDate.slice(1);
        
    // const returnAV = () => {
        return (
            <section className="">
                <div className="container mx-auto py-6 px-4">
                    {/* Hero section con fecha y hora */}
                    <div className="mb-8 bg-gradient-to-r from-blue-50 to-indigo-50 rounded-lg p-6 shadow-sm  dark:from-blue-600 dark:to-indigo-400">
                        <div className="flex flex-col justify-between items-start gap-4 md:items-center md:flex-row">
                            <div>
                                <h1 className="text-2xl font-bold tracking-tight">
                                    Bienvenido, {user?.name}
                                </h1>
                                <div className="flex items-center mt-2 text-muted-foreground dark:text-zinc-300">
                                    <CalendarDays className="mr-2 h-4 w-4" />
                                    <span>{capitalizedDate}</span>
                                </div>
                            </div>
                            <div className="flex items-center text-xl font-semibold">
                                <Clock className="mr-2 h-5 w-5" />
                                <span>{formattedTime}</span>
                            </div>
                        </div>
                    </div>
    
                    {/* Main content and sidebar */}
                    <div className="grid grid-cols-1 lg:grid-cols-[1fr_300px] gap-6">
                        {/* Main content */}
                        <div className="space-y-6">
                            <h2 className="text-xl font-semibold tracking-tight">
                                Noticias y Anuncios
                            </h2>
    
                            <div className="space-y-4">
                                {news.map((item) => (
                                    <Card
                                        key={item.id}
                                        className={
                                            item.important
                                                ? "border-blue-200 bg-blue-50 dark:border-blue-600 dark:bg-blue-900"
                                                : ""
                                        }
                                    >
                                        <CardHeader className="pb-2">
                                            <div className="flex justify-between items-start">
                                                <div>
                                                    <CardTitle className="text-lg">
                                                        {item.title}
                                                    </CardTitle>
                                                    <CardDescription>
                                                        {item.date}
                                                    </CardDescription>
                                                </div>
                                                <Badge variant="outline">
                                                    {item.category}
                                                </Badge>
                                            </div>
                                        </CardHeader>
                                        <CardContent>
                                            <p>{item.content}</p>
                                        </CardContent>
                                        {item.important && (
                                            <CardFooter className="pt-0">
                                                <Button
                                                    variant="link"
                                                    className="p-0 h-auto group"
                                                >
                                                    Leer más{" "}
                                                    <ChevronRight className="h-4 w-4 ml-1 group-hover:translate-x-1 transition-transform" />
                                                </Button>
                                            </CardFooter>
                                        )}
                                    </Card>
                                ))}
                            </div>
    
                            <div className="pt-4">
                                <h2 className="text-xl font-semibold tracking-tight mb-4">
                                    Cursos Recomendados
                                </h2>
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                    <Card>
                                        <CardHeader>
                                            <CardTitle>
                                                Inteligencia Artificial
                                            </CardTitle>
                                            <CardDescription>
                                                Fundamentos y aplicaciones prácticas
                                            </CardDescription>
                                        </CardHeader>
                                        <CardContent>
                                            <p className="text-sm text-muted-foreground">
                                                Aprende los conceptos básicos de IA
                                                y cómo aplicarlos en proyectos
                                                reales.
                                            </p>
                                        </CardContent>
                                        <CardFooter>
                                            <Button
                                                variant="outline"
                                                className="w-full"
                                            >
                                                Ver detalles
                                            </Button>
                                        </CardFooter>
                                    </Card>
    
                                    <Card>
                                        <CardHeader>
                                            <CardTitle>Marketing Digital</CardTitle>
                                            <CardDescription>
                                                Estrategias para el crecimiento
                                                online
                                            </CardDescription>
                                        </CardHeader>
                                        <CardContent>
                                            <p className="text-sm text-muted-foreground">
                                                Domina las técnicas más efectivas
                                                para promocionar productos y
                                                servicios en internet.
                                            </p>
                                        </CardContent>
                                        <CardFooter>
                                            <Button
                                                variant="outline"
                                                className="w-full"
                                            >
                                                Ver detalles
                                            </Button>
                                        </CardFooter>
                                    </Card>
                                </div>
                            </div>
                        </div>
    
                        {/* Sidebar - Cursos inscritos */}
                        <div className="space-y-6">
                            <div>
                                <div className="flex items-center mb-6">
                                    <Bell className="mr-2 h-5 w-5" />
                                    <h2 className="text-xl font-semibold tracking-tight ">
                                        Recordatorios
                                    </h2>
                                </div>
    
                                <div className="space-y-3">
                                    <div className="bg-amber-50 border border-amber-200 rounded-md p-3 text-sm dark:bg-amber-900 dark:border-amber-700">
                                        <div className="font-medium">
                                            Entrega pendiente
                                        </div>
                                        <div className="text-muted-foreground dark:text-zinc-300">
                                            Proyecto final - Programación Web
                                        </div>
                                        <div className="text-xs mt-1 text-amber-700 dark:text-amber-500">
                                            Vence en 3 días
                                        </div>
                                    </div>
    
                                    <div className="bg-blue-50 border border-blue-200 rounded-md p-3 text-sm dark:bg-blue-900 dark:border-blue-700">
                                        <div className="font-medium">
                                            Próximo examen
                                        </div>
                                        <div className="text-muted-foreground dark:text-zinc-300">
                                            Evaluación parcial - Curso 2025
                                        </div>
                                        <div className="text-xs mt-1 text-blue-700 dark:text-blue-300">
                                            30 de Marzo, 2025
                                        </div>
                                    </div>
                                    <Link
                                        href="#"
                                        className="p-3 flex items-center justify-center text-sm font-medium text-blue-600 bg-blue-50 border border-blue-200 rounded-md hover:bg-blue-100 dark:bg-blue-900 dark:border-blue-700 dark:text-blue-300 dark:hover:bg-blue-800 group"
                                    >
                                        Ver mas recordatorios
                                        <ChevronRight className="size-5 ml-1 group-hover:translate-x-1 transition-transform" />
                                    </Link>
                                </div>
                            </div>
    
                            <Separator className="bg-zinc-400 dark:bg-zinc-700" />
    
                            <div className="flex items-center">
                                <BookOpen className="mr-2 h-5 w-5" />
                                <h2 className="text-xl font-semibold tracking-tight">
                                    Mis Cursos
                                </h2>
                            </div>
    
                            <div className="space-y-4">
                                {enrolledCourses.map((course) => (
                                    <Card
                                        key={course.id}
                                        className="overflow-hidden"
                                    >
                                        <CardHeader className="pb-2">
                                            <CardTitle className="text-base">
                                                {course.title}
                                            </CardTitle>
                                        </CardHeader>
                                        <CardContent className="pb-3">
                                            <div className="space-y-2">
                                                <div className="flex justify-between text-sm">
                                                    <span>Progreso:</span>
                                                    <span className="font-medium">
                                                        {course.progress}%
                                                    </span>
                                                </div>
                                                <div className="w-full h-2 bg-gray-100 rounded-full overflow-hidden">
                                                    <div
                                                        className="h-full bg-blue-500 rounded-full"
                                                        style={{
                                                            width: `${course.progress}%`,
                                                        }}
                                                    ></div>
                                                </div>
    
                                                {course.nextClass && (
                                                    <div className="text-xs text-muted-foreground mt-2">
                                                        <span className="font-medium">
                                                            Próxima clase:
                                                        </span>{" "}
                                                        {course.nextClass}
                                                    </div>
                                                )}
                                            </div>
                                        </CardContent>
                                        <CardFooter className="pt-0">
                                            <Link
                                                href={`/aula-virtual/${course.id}`}
                                                className="w-full"
                                            >
                                                <Button
                                                    variant="ghost"
                                                    size="sm"
                                                    className="w-full justify-between"
                                                >
                                                    Continuar
                                                    <ChevronRight className="h-4 w-4 ml-1" />
                                                </Button>
                                            </Link>
                                        </CardFooter>
                                    </Card>
                                ))}
                            </div>
                        </div>
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

    // switch (user?.role) {
    //     case "ROLE_STUDENT":
    //       return returnAV();
    //     case "ROLE_ADMIN":
    //       router.push("/admin");
    //       break;
    //     case "ROLE_TEACHER":
    //       router.push("/admin/cursos");
    //       break;
    //     case "ROLE_PARENT":
    //       router.push("/padres");
    //       break;
    //     case "ROLE_CASHER":
    //       router.push("/admin/caja");
    //       break;
    //     case "ROLE_ADMIN_COURSES":
    //       router.push("/admin/cursos");
    //       break;
    //     case "ROLE_ADMIN_USERS":
    //       router.push("/admin/usuarios");
    //       break;
    //     case "ROLE_ADMIN_VC":
    //       router.push("/admin/aula-virtual");
    //       break;
    //     default:
    //       router.push("/login");
    //       break;
    //   }
}

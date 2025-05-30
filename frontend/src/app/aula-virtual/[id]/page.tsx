"use client";

import { use, useEffect, useState } from "react";
import Link from "next/link";
import {
    ChevronRight,
    ArrowLeft,
    ExternalLink,
    ArrowRight,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { useAuthStore } from "@/context/store";

type CourseLink = {
    id: string;
    title: string;
    url: string;
};

type Content = {
    body: string;
    links: CourseLink[];
};

type Subsection = {
    id: string;
    title: string;
    content: Content;
};

type Section = {
    id: string;
    title: string;
    subsections: Subsection[];
};

type Course = {
    createdAt: string;
    updatedAt: string | null;
    modifiedBy: string | null;
    createdBy: string;
    id: string;
    title: string;
    description: string;
    status: string;
    monthlyPrice: number;
    studentsIds: string[] | null;
    teacherId: string[];
    sections: Section[];
};

export default function CourseDetail({ params }: { params: Promise<{ id: string }> }) {
    const {user} = useAuthStore();
    const { id } = use(params);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [courseData, setCourse] = useState<Course>({
        createdAt: "",
        updatedAt: null,
        modifiedBy: null,
        createdBy: "",
        id: id,
        title: "",
        description: "",
        status: "",
        monthlyPrice: 0,
        studentsIds: null,
        teacherId: [],
        sections: [
            {
                id: "",
                title: "",
                subsections: [
                    {
                        id: "",
                        title: "",
                        content: {
                            body: "",
                            links: [
                                {
                                    id: "",
                                    title: "",
                                    url: "",
                                }
                            ],
                        },
                    },
                ],
            },
        ],
    });

    const [activeSection, setActiveSection] = useState(
        courseData.sections[0].id
    );
    const [activeSubsection, setActiveSubsection] = useState(
        courseData.sections[0].subsections[0].id
    );

    // Encontrar la sección y subsección activas
    const currentSection = courseData.sections.find(
        (section) => section.id === activeSection
    );
    const currentSubsection = currentSection?.subsections.find(
        (subsection) => subsection.id === activeSubsection
    );

    useEffect(()=>{
        fetchCourseData();
    },[id])

    const fetchCourseData = async () => {
        if (!user?.token) return;
        try {
            setIsLoading(true);
            setError(null);
            
            // Obtener las secciones del curso
            const sectionsResponse = await fetch(
                `https://instituto.sistemataup.online/api/cursos/obtenerContenido/${id}`,
                {
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${user?.token}`,
                    },
                }
            );

            if (!sectionsResponse.ok) {
                throw new Error("Error al obtener las secciones del curso");
            }

            // Verificar si la respuesta tiene contenido
            const responseText = await sectionsResponse.text();
            let sectionsData = [];
            
            if (responseText) {
                try {
                    if(user.role.includes("ROLE_TEACHER")){
                    sectionsData = JSON.parse(responseText).sections.filter(
                        (section: any) => section.teacherId === user.id
                    );
                    console.log(JSON.parse(responseText).sections);
                    
                    } else {
                        sectionsData = JSON.parse(responseText).sections;
                    }
                } catch (parseError) {
                    console.error("Error al parsear la respuesta:", parseError);
                    sectionsData = [];
                }
            }

            // Si no hay secciones o la respuesta está vacía, mantenemos el estado inicial
            if (!sectionsData || sectionsData.length === 0) {
                setCourse((prevCourse) => ({
                    ...prevCourse,
                    id: id,
                    sections: [],
                }));
            } else {
                // Actualizar el estado del curso con las secciones
                setCourse((prevCourse) => ({
                    ...prevCourse,
                    sections: sectionsData.map((section: any) => ({
                        id: section.id,
                        title: section.title,
                        description: section.description,
                        subsections: section.subSections?.map(
                            (subsection: any) => ({
                                id: subsection.id,
                                title: subsection.title,
                                body: subsection.body || "",
                                filesIds: subsection.files ? subsection.files.map(
                                    (file: any) => ({
                                        id: file.id,
                                        title: file.name,
                                        url: file.link,
                                        file: null,
                                    })
                                ) : [],
                            })
                        ),
                    })),
                }));
            }
        } catch (err) {
            console.error("Error fetching course data:", err);
            // Si hay un error, mostramos la interfaz para crear secciones
            setCourse((prevCourse) => ({
                ...prevCourse,
                id: id,
                sections: [],
            }));
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="container mx-auto py-6 min-h-[calc(100vh-65px)]">
            <div className="mb-6">
                <Link
                    href="/"
                    className="flex items-center text-sm text-muted-foreground hover:text-foreground group transition-colors"
                >
                    <ArrowLeft className="mr-2 h-4 w-4 group-hover:-translate-x-1 transition-transform" />
                    Volver a la página principal
                </Link>
            </div>

            <div className="grid grid-cols-1 gap-6 md:grid-cols-[300px_1fr]">
                {/* Sidebar de navegación */}
                <div className="space-y-4">
                    <Card>
                        <CardHeader className="pb-3">
                            <CardTitle>{courseData.title}</CardTitle>
                            <CardDescription>
                                {courseData.description}
                            </CardDescription>
                            <div className="mt-2 text-sm">
                                <div className="flex justify-between">
                                    <span>Precio:</span>
                                    <span className="font-medium">
                                        $
                                        {courseData.monthlyPrice.toLocaleString(
                                            "es-ES"
                                        )}
                                    </span>
                                </div>
                            </div>
                        </CardHeader>
                        <CardContent className="p-0">
                            <Tabs defaultValue="contenido" className="w-full">
                                <TabsList className="grid w-full grid-cols-2 dark:bg-zinc-900">
                                    <TabsTrigger value="contenido" className="">
                                        Contenido
                                    </TabsTrigger>
                                    <TabsTrigger value="info">
                                        Información
                                    </TabsTrigger>
                                </TabsList>
                                <TabsContent value="contenido" className="p-4">
                                    <nav className="space-y-2">
                                        {courseData.sections.map((section) => (
                                            <div
                                                key={section.id}
                                                className="space-y-1"
                                            >
                                                <Button
                                                    variant="ghost"
                                                    className={`w-full justify-start font-medium ${
                                                        activeSection ===
                                                        section.id
                                                            ? "bg-muted"
                                                            : ""
                                                    }`}
                                                    onClick={() => {
                                                        setActiveSection(
                                                            section.id
                                                        );
                                                        // Seleccionar automáticamente la primera subsección de esta sección
                                                        if (
                                                            section.subsections
                                                                .length > 0
                                                        ) {
                                                            setActiveSubsection(
                                                                section
                                                                    .subsections[0]
                                                                    .id
                                                            );
                                                        }
                                                    }}
                                                >
                                                    {section.title}
                                                </Button>

                                                {activeSection ===
                                                    section.id && (
                                                    <div className="ml-4 space-y-1">
                                                        {section.subsections.map(
                                                            (subsection) => (
                                                                <Button
                                                                    key={
                                                                        subsection.id
                                                                    }
                                                                    variant="ghost"
                                                                    size="sm"
                                                                    className={`w-full justify-start text-sm ${
                                                                        activeSubsection ===
                                                                        subsection.id
                                                                            ? "bg-muted"
                                                                            : ""
                                                                    }`}
                                                                    onClick={() =>
                                                                        setActiveSubsection(
                                                                            subsection.id
                                                                        )
                                                                    }
                                                                >
                                                                    <ChevronRight className="mr-1 h-4 w-4" />
                                                                    {
                                                                        subsection.title
                                                                    }
                                                                </Button>
                                                            )
                                                        )}
                                                    </div>
                                                )}
                                            </div>
                                        ))}
                                    </nav>
                                </TabsContent>
                                <TabsContent value="info" className="p-4">
                                    <div className="space-y-2 text-sm">
                                        <div className="flex justify-between">
                                            <span>Creado:</span>
                                            <span>
                                                {new Date(
                                                    courseData.createdAt
                                                ).toLocaleDateString()}
                                            </span>
                                        </div>
                                        <div className="flex justify-between">
                                            <span>Estado:</span>
                                            <span className="font-medium">
                                                {courseData.status}
                                            </span>
                                        </div>
                                        <div className="flex justify-between">
                                            <span>Creado por:</span>
                                            <span>{courseData.createdBy}</span>
                                        </div>
                                    </div>
                                </TabsContent>
                            </Tabs>
                        </CardContent>
                    </Card>
                </div>

                {/* Contenido principal */}
                <div className="space-y-4">
                    <Card>
                        <CardHeader>
                            <div className="text-sm text-muted-foreground">
                                {currentSection?.title} /{" "}
                                {currentSubsection?.title}
                            </div>
                            <CardTitle>{currentSubsection?.title}</CardTitle>
                        </CardHeader>
                        <CardContent>
                            {currentSubsection && (
                                <div className="space-y-6">
                                    <div
                                        dangerouslySetInnerHTML={{
                                            __html: currentSubsection.content
                                                .body,
                                        }}
                                    />

                                    {currentSubsection.content.links.length >
                                        0 && (
                                        <>
                                            <Separator />
                                            <div>
                                                <h3 className="mb-3 text-lg font-medium">
                                                    Recursos
                                                </h3>
                                                <div className="space-y-2">
                                                    {currentSubsection.content.links.map(
                                                        (link, index) => (
                                                            <a
                                                                key={index}
                                                                href={link.url}
                                                                target="_blank"
                                                                rel="noopener noreferrer"
                                                                className="flex items-center text-blue-600 hover:underline"
                                                            >
                                                                <ExternalLink className="mr-2 h-4 w-4" />
                                                                {link.title}
                                                            </a>
                                                        )
                                                    )}
                                                </div>
                                            </div>
                                        </>
                                    )}
                                </div>
                            )}
                        </CardContent>
                    </Card>

                    <div className="flex justify-between">
                        <Button variant="outline">
                            <ArrowLeft className="mr-2 h-4 w-4" />
                            Anterior
                        </Button>
                        <Button>
                            Siguiente
                            <ArrowRight className="ml-2 h-4 w-4" />
                        </Button>
                    </div>
                </div>
            </div>
        </div>
    );
}

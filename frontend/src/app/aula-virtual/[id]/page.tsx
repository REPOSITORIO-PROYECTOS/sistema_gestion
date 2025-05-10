"use client";

import { useState } from "react";
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
    teacherId: string;
    sections: Section[];
};

export default function CourseDetail({ params }: { params: Promise<{ id: string }> }) {
    const courseData: Course = {
        createdAt: "07-02-2025T18:53",
        updatedAt: null,
        modifiedBy: null,
        createdBy: "ADMIN",
        id: "67a656e0f9ff8d5d561d9a6e",
        title: "curso 2025",
        description: "guardado como usuario.",
        status: "ACTIVE",
        monthlyPrice: 7000.99,
        studentsIds: null,
        teacherId: "67a65539fff1b421994b0b33",
        sections: [
            {
                id: "section-1",
                title: "Introducción al curso",
                subsections: [
                    {
                        id: "subsection-1-1",
                        title: "Bienvenida",
                        content: {
                            body: "<h2>Bienvenido al curso</h2><p>Este curso te guiará a través de los conceptos fundamentales.</p>",
                            links: [
                                {
                                    id: "sfd",
                                    title: "Guía del estudiante",
                                    url: "https://example.com/guia-estudiante.pdf",
                                },
                                {
                                    id: "sfd",
                                    title: "Normas del curso",
                                    url: "https://example.com/normas.pdf",
                                },
                            ],
                        },
                    },
                ],
            },
            {
                id: "section-2",
                title: "Módulo 1: Conceptos básicos",
                subsections: [
                    {
                        id: "subsection-2-1",
                        title: "Introducción a los fundamentos",
                        content: {
                            body: "<p>En este módulo aprenderás los conceptos esenciales.</p><ul><li>Concepto 1</li><li>Concepto 2</li></ul>",
                            links: [
                                {
                                    id: "sfd",
                                    title: "Material de apoyo",
                                    url: "https://example.com/material-apoyo.docx",
                                },
                                {
                                    id: "sfd",
                                    title: "Lectura recomendada",
                                    url: "https://example.com/lectura.pdf",
                                },
                            ],
                        },
                    },
                    {
                        id: "subsection-2-2",
                        title: "Ejercicios prácticos",
                        content: {
                            body: "<p>Realiza los siguientes ejercicios para practicar.</p><ol><li>Ejercicio 1</li><li>Ejercicio 2</li></ol>",
                            links: [],
                        },
                    },
                ],
            },
        ],
    };

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

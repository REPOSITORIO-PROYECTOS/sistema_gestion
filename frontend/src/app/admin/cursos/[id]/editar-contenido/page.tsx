"use client";

import { useState, useEffect, use } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import {
    ArrowLeft,
    Plus,
    Pencil,
    Trash2,
    ChevronRight,
    GripVertical,
    ExternalLink,
    Save,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog";
import {
    Accordion,
    AccordionContent,
    AccordionItem,
    AccordionTrigger,
} from "@/components/ui/accordion";
// import { useAuth } from "@/components/auth-provider"

// Tipos
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
    id: string;
    title: string;
    description: string;
    status: "ACTIVE" | "PENDING" | "COMPLETED";
    price: number;
    sections: Section[];
};

export default function EditCourseContentPage({
    params,
}: {
    params: { id: string };
}) {
    const { id } = use(params);
    //   const { user, isLoading } = useAuth()
    const router = useRouter();
    const [isClient, setIsClient] = useState(false);

    // Estado para el curso
    const [course, setCourse] = useState<Course>({
        id: id,
        title: "Curso 2025",
        description: "Guardado como usuario",
        status: "ACTIVE",
        price: 7000.99,
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
                                    id: "link-1-1-1",
                                    title: "Guía del estudiante",
                                    url: "https://example.com/guia-estudiante.pdf",
                                },
                                {
                                    id: "link-1-1-2",
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
                                    id: "link-2-1-1",
                                    title: "Material de apoyo",
                                    url: "https://example.com/material-apoyo.docx",
                                },
                                {
                                    id: "link-2-1-2",
                                    title: "Lectura recomendada",
                                    url: "https://example.com/lectura.pdf",
                                },
                            ],
                        },
                    },
                ],
            },
        ],
    });

    // Estados para diálogos y formularios
    const [activeSection, setActiveSection] = useState<string | null>(null);
    const [activeSubsection, setActiveSubsection] = useState<string | null>(
        null
    );
    const [isSectionDialogOpen, setIsSectionDialogOpen] = useState(false);
    const [isSubsectionDialogOpen, setIsSubsectionDialogOpen] = useState(false);
    const [isLinkDialogOpen, setIsLinkDialogOpen] = useState(false);
    const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
    const [itemToDelete, setItemToDelete] = useState<{
        id: string;
        type: "section" | "subsection" | "link";
    } | null>(null);

    // Estados para formularios
    const [sectionForm, setSectionForm] = useState({
        id: "",
        title: "",
    });

    const [subsectionForm, setSubsectionForm] = useState({
        id: "",
        title: "",
        content: {
            body: "",
            links: [] as CourseLink[],
        },
    });

    const [linkForm, setLinkForm] = useState({
        id: "",
        title: "",
        url: "",
    });

    //   useEffect(() => {
    //     setIsClient(true)
    //     // Verificar si el usuario está autenticado y es administrador
    //     if (!isLoading && (!user || user.role !== "admin")) {
    //       router.push("/login")
    //     }
    //   }, [user, isLoading, router])

    // Resetear formulario de sección
    const resetSectionForm = () => {
        setSectionForm({
            id: "",
            title: "",
        });
    };

    // Resetear formulario de subsección
    const resetSubsectionForm = () => {
        setSubsectionForm({
            id: "",
            title: "",
            content: {
                body: "",
                links: [],
            },
        });
    };

    // Resetear formulario de enlace
    const resetLinkForm = () => {
        setLinkForm({
            id: "",
            title: "",
            url: "",
        });
    };

    // Abrir formulario para editar sección
    const handleEditSection = (section: Section) => {
        setSectionForm({
            id: section.id,
            title: section.title,
        });
        setIsSectionDialogOpen(true);
    };

    // Abrir formulario para editar subsección
    const handleEditSubsection = (subsection: Subsection) => {
        setSubsectionForm({
            id: subsection.id,
            title: subsection.title,
            content: {
                body: subsection.content.body,
                links: [...subsection.content.links],
            },
        });
        setIsSubsectionDialogOpen(true);
    };

    // Abrir formulario para editar enlace
    const handleEditLink = (link: CourseLink) => {
        setLinkForm({
            id: link.id,
            title: link.title,
            url: link.url,
        });
        setIsLinkDialogOpen(true);
    };

    // Guardar sección (nueva o editada)
    const handleSaveSection = () => {
        if (sectionForm.id) {
            // Editar existente
            setCourse({
                ...course,
                sections: course.sections.map((section) =>
                    section.id === sectionForm.id
                        ? { ...section, title: sectionForm.title }
                        : section
                ),
            });
        } else {
            // Crear nueva
            const newId = `section-${Date.now()}`;
            setCourse({
                ...course,
                sections: [
                    ...course.sections,
                    {
                        id: newId,
                        title: sectionForm.title,
                        subsections: [],
                    },
                ],
            });
        }

        setIsSectionDialogOpen(false);
        resetSectionForm();
    };

    // Guardar subsección (nueva o editada)
    const handleSaveSubsection = () => {
        if (!activeSection) return;

        if (subsectionForm.id) {
            // Editar existente
            setCourse({
                ...course,
                sections: course.sections.map((section) =>
                    section.id === activeSection
                        ? {
                              ...section,
                              subsections: section.subsections.map(
                                  (subsection) =>
                                      subsection.id === subsectionForm.id
                                          ? {
                                                ...subsection,
                                                title: subsectionForm.title,
                                                content: {
                                                    body: subsectionForm.content
                                                        .body,
                                                    links: subsectionForm
                                                        .content.links,
                                                },
                                            }
                                          : subsection
                              ),
                          }
                        : section
                ),
            });
        } else {
            // Crear nueva
            const newId = `subsection-${Date.now()}`;
            setCourse({
                ...course,
                sections: course.sections.map((section) =>
                    section.id === activeSection
                        ? {
                              ...section,
                              subsections: [
                                  ...section.subsections,
                                  {
                                      id: newId,
                                      title: subsectionForm.title,
                                      content: {
                                          body: subsectionForm.content.body,
                                          links: subsectionForm.content.links,
                                      },
                                  },
                              ],
                          }
                        : section
                ),
            });
        }

        setIsSubsectionDialogOpen(false);
        resetSubsectionForm();
    };

    // Guardar enlace (nuevo o editado)
    const handleSaveLink = () => {
        if (!activeSection || !activeSubsection) return;

        const updatedLinks = subsectionForm.content.links.slice();

        if (linkForm.id) {
            // Editar existente
            const linkIndex = updatedLinks.findIndex(
                (link) => link.id === linkForm.id
            );
            if (linkIndex !== -1) {
                updatedLinks[linkIndex] = {
                    id: linkForm.id,
                    title: linkForm.title,
                    url: linkForm.url,
                };
            }
        } else {
            // Crear nuevo
            const newId = `link-${Date.now()}`;
            updatedLinks.push({
                id: newId,
                title: linkForm.title,
                url: linkForm.url,
            });
        }

        setSubsectionForm({
            ...subsectionForm,
            content: {
                ...subsectionForm.content,
                links: updatedLinks,
            },
        });

        setIsLinkDialogOpen(false);
        resetLinkForm();
    };

    // Confirmar eliminación
    const handleDelete = () => {
        if (!itemToDelete) return;

        if (itemToDelete.type === "section") {
            setCourse({
                ...course,
                sections: course.sections.filter(
                    (section) => section.id !== itemToDelete.id
                ),
            });
        } else if (itemToDelete.type === "subsection") {
            setCourse({
                ...course,
                sections: course.sections.map((section) => ({
                    ...section,
                    subsections: section.subsections.filter(
                        (subsection) => subsection.id !== itemToDelete.id
                    ),
                })),
            });
        } else if (itemToDelete.type === "link") {
            // Eliminar enlace del formulario de subsección
            setSubsectionForm({
                ...subsectionForm,
                content: {
                    ...subsectionForm.content,
                    links: subsectionForm.content.links.filter(
                        (link) => link.id !== itemToDelete.id
                    ),
                },
            });
        }

        setIsDeleteDialogOpen(false);
        setItemToDelete(null);
    };

    // Guardar todos los cambios
    const handleSaveCourse = () => {
        // Aquí se enviarían los datos al servidor
        alert("Curso guardado correctamente");
    };

    //   if (!isClient || isLoading) {
    //     return <div className="flex h-screen items-center justify-center">Cargando...</div>
    //   }

    return (
        <div className="container mx-auto py-6 px-4">
            <div className="mb-6 flex items-center justify-between">
                <div className="flex items-center">
                    <Button
                        variant="outline"
                        size="icon"
                        asChild
                        className="mr-4"
                    >
                        <Link href="/admin/cursos">
                            <ArrowLeft className="h-4 w-4" />
                        </Link>
                    </Button>
                    <div>
                        <h1 className="text-2xl font-bold tracking-tight">
                            {course.title}
                        </h1>
                        <p className="text-muted-foreground">
                            Editar contenido del curso
                        </p>
                    </div>
                </div>
                <Button onClick={handleSaveCourse}>
                    <Save className="mr-2 h-4 w-4" />
                    Guardar Cambios
                </Button>
            </div>

            <div className="grid grid-cols-1 gap-6 md:grid-cols-[300px_1fr]">
                {/* Panel de navegación */}
                <div className="space-y-4">
                    <Card>
                        <CardHeader className="pb-3">
                            <CardTitle>Estructura del Curso</CardTitle>
                            <CardDescription>
                                Organice las secciones y subsecciones del curso
                            </CardDescription>
                        </CardHeader>
                        <CardContent className="p-0">
                            <div className="p-4">
                                <Button
                                    className="w-full"
                                    onClick={() => {
                                        resetSectionForm();
                                        setIsSectionDialogOpen(true);
                                    }}
                                >
                                    <Plus className="mr-2 h-4 w-4" />
                                    Agregar Sección
                                </Button>
                            </div>

                            <Accordion
                                type="single"
                                collapsible
                                className="w-full"
                            >
                                {course.sections.map((section) => (
                                    <AccordionItem
                                        key={section.id}
                                        value={section.id}
                                    >
                                        <AccordionTrigger className="px-4 hover:bg-muted">
                                            <div className="flex items-center text-left">
                                                <GripVertical className="mr-2 h-4 w-4 text-muted-foreground" />
                                                <span>{section.title}</span>
                                            </div>
                                        </AccordionTrigger>
                                        <AccordionContent>
                                            <div className="px-4 py-2 space-y-2">
                                                <div className="flex justify-between items-center">
                                                    <Button
                                                        variant="ghost"
                                                        size="sm"
                                                        onClick={() =>
                                                            handleEditSection(
                                                                section
                                                            )
                                                        }
                                                    >
                                                        <Pencil className="mr-2 h-3 w-3" />
                                                        Editar
                                                    </Button>
                                                    <Button
                                                        variant="ghost"
                                                        size="sm"
                                                        className="text-red-500 hover:text-red-700 hover:bg-red-50"
                                                        onClick={() => {
                                                            setItemToDelete({
                                                                id: section.id,
                                                                type: "section",
                                                            });
                                                            setIsDeleteDialogOpen(
                                                                true
                                                            );
                                                        }}
                                                    >
                                                        <Trash2 className="mr-2 h-3 w-3" />
                                                        Eliminar
                                                    </Button>
                                                </div>

                                                <Button
                                                    variant="outline"
                                                    size="sm"
                                                    className="w-full justify-start"
                                                    onClick={() => {
                                                        setActiveSection(
                                                            section.id
                                                        );
                                                        resetSubsectionForm();
                                                        setIsSubsectionDialogOpen(
                                                            true
                                                        );
                                                    }}
                                                >
                                                    <Plus className="mr-2 h-3 w-3" />
                                                    Agregar Subsección
                                                </Button>

                                                <div className="pl-4 space-y-1 mt-2">
                                                    {section.subsections.map(
                                                        (subsection) => (
                                                            <div
                                                                key={
                                                                    subsection.id
                                                                }
                                                                className="flex items-center justify-between py-1 px-2 rounded-md hover:bg-muted group"
                                                            >
                                                                <button
                                                                    className="flex items-center text-sm text-left w-full"
                                                                    onClick={() => {
                                                                        setActiveSection(
                                                                            section.id
                                                                        );
                                                                        setActiveSubsection(
                                                                            subsection.id
                                                                        );
                                                                    }}
                                                                >
                                                                    <ChevronRight className="mr-1 h-3 w-3" />
                                                                    <span>
                                                                        {
                                                                            subsection.title
                                                                        }
                                                                    </span>
                                                                </button>
                                                                <div className="flex space-x-1 opacity-0 group-hover:opacity-100">
                                                                    <Button
                                                                        variant="ghost"
                                                                        size="icon"
                                                                        className="h-6 w-6"
                                                                        onClick={(
                                                                            e
                                                                        ) => {
                                                                            e.stopPropagation();
                                                                            setActiveSection(
                                                                                section.id
                                                                            );
                                                                            handleEditSubsection(
                                                                                subsection
                                                                            );
                                                                        }}
                                                                    >
                                                                        <Pencil className="h-3 w-3" />
                                                                    </Button>
                                                                    <Button
                                                                        variant="ghost"
                                                                        size="icon"
                                                                        className="h-6 w-6 text-red-500 hover:text-red-700 hover:bg-red-50"
                                                                        onClick={(
                                                                            e
                                                                        ) => {
                                                                            e.stopPropagation();
                                                                            setItemToDelete(
                                                                                {
                                                                                    id: subsection.id,
                                                                                    type: "subsection",
                                                                                }
                                                                            );
                                                                            setIsDeleteDialogOpen(
                                                                                true
                                                                            );
                                                                        }}
                                                                    >
                                                                        <Trash2 className="h-3 w-3" />
                                                                    </Button>
                                                                </div>
                                                            </div>
                                                        )
                                                    )}

                                                    {section.subsections
                                                        .length === 0 && (
                                                        <div className="text-xs text-muted-foreground py-1 px-2">
                                                            No hay subsecciones
                                                        </div>
                                                    )}
                                                </div>
                                            </div>
                                        </AccordionContent>
                                    </AccordionItem>
                                ))}
                            </Accordion>

                            {course.sections.length === 0 && (
                                <div className="p-4 text-center text-muted-foreground">
                                    No hay secciones. Agregue una para comenzar.
                                </div>
                            )}
                        </CardContent>
                    </Card>
                </div>

                {/* Panel de edición */}
                <div className="space-y-4">
                    {activeSection && activeSubsection ? (
                        <Card>
                            <CardHeader>
                                <CardTitle>
                                    {
                                        course.sections.find(
                                            (s) => s.id === activeSection
                                        )?.title
                                    }{" "}
                                    /
                                    {
                                        course.sections
                                            .find((s) => s.id === activeSection)
                                            ?.subsections.find(
                                                (ss) =>
                                                    ss.id === activeSubsection
                                            )?.title
                                    }
                                </CardTitle>
                                <CardDescription>
                                    Edite el contenido de esta subsección
                                </CardDescription>
                            </CardHeader>
                            <CardContent>
                                <Tabs defaultValue="content">
                                    <TabsList className="mb-4">
                                        <TabsTrigger value="content">
                                            Contenido
                                        </TabsTrigger>
                                        <TabsTrigger value="resources">
                                            Recursos
                                        </TabsTrigger>
                                    </TabsList>

                                    <TabsContent
                                        value="content"
                                        className="space-y-4"
                                    >
                                        <div className="space-y-2">
                                            <Label htmlFor="subsection-title">
                                                Título de la subsección
                                            </Label>
                                            <Input
                                                id="subsection-title"
                                                value={subsectionForm.title}
                                                onChange={(e) =>
                                                    setSubsectionForm({
                                                        ...subsectionForm,
                                                        title: e.target.value,
                                                    })
                                                }
                                                placeholder="Título de la subsección"
                                            />
                                        </div>

                                        <div className="space-y-2">
                                            <Label htmlFor="subsection-content">
                                                Contenido
                                            </Label>
                                            <Textarea
                                                id="subsection-content"
                                                value={
                                                    subsectionForm.content.body
                                                }
                                                onChange={(e) =>
                                                    setSubsectionForm({
                                                        ...subsectionForm,
                                                        content: {
                                                            ...subsectionForm.content,
                                                            body: e.target
                                                                .value,
                                                        },
                                                    })
                                                }
                                                placeholder="Contenido HTML de la subsección"
                                                rows={10}
                                                className="font-mono text-sm"
                                            />
                                            <p className="text-xs text-muted-foreground">
                                                Puede utilizar HTML para dar
                                                formato al contenido (p, h1-h6,
                                                ul, li, etc.)
                                            </p>
                                        </div>

                                        <div className="flex justify-end">
                                            <Button
                                                onClick={handleSaveSubsection}
                                            >
                                                Guardar Cambios
                                            </Button>
                                        </div>
                                    </TabsContent>

                                    <TabsContent
                                        value="resources"
                                        className="space-y-4"
                                    >
                                        <div className="flex justify-between items-center">
                                            <h3 className="text-lg font-medium">
                                                Enlaces y Recursos
                                            </h3>
                                            <Button
                                                variant="outline"
                                                size="sm"
                                                onClick={() => {
                                                    resetLinkForm();
                                                    setIsLinkDialogOpen(true);
                                                }}
                                            >
                                                <Plus className="mr-2 h-4 w-4" />
                                                Agregar Enlace
                                            </Button>
                                        </div>

                                        <Separator />

                                        {subsectionForm.content.links.length >
                                        0 ? (
                                            <div className="space-y-2">
                                                {subsectionForm.content.links.map(
                                                    (link) => (
                                                        <div
                                                            key={link.id}
                                                            className="flex items-center justify-between p-2 rounded-md border hover:bg-muted"
                                                        >
                                                            <div className="flex items-center">
                                                                <ExternalLink className="mr-2 h-4 w-4 text-muted-foreground" />
                                                                <div>
                                                                    <div className="font-medium">
                                                                        {
                                                                            link.title
                                                                        }
                                                                    </div>
                                                                    <div className="text-xs text-muted-foreground truncate max-w-md">
                                                                        {
                                                                            link.url
                                                                        }
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div className="flex space-x-2">
                                                                <Button
                                                                    variant="ghost"
                                                                    size="sm"
                                                                    onClick={() =>
                                                                        handleEditLink(
                                                                            link
                                                                        )
                                                                    }
                                                                >
                                                                    <Pencil className="h-4 w-4" />
                                                                </Button>
                                                                <Button
                                                                    variant="ghost"
                                                                    size="sm"
                                                                    className="text-red-500 hover:text-red-700 hover:bg-red-50"
                                                                    onClick={() => {
                                                                        setItemToDelete(
                                                                            {
                                                                                id: link.id,
                                                                                type: "link",
                                                                            }
                                                                        );
                                                                        setIsDeleteDialogOpen(
                                                                            true
                                                                        );
                                                                    }}
                                                                >
                                                                    <Trash2 className="h-4 w-4" />
                                                                </Button>
                                                            </div>
                                                        </div>
                                                    )
                                                )}
                                            </div>
                                        ) : (
                                            <div className="text-center py-8 text-muted-foreground">
                                                No hay enlaces. Agregue uno para
                                                comenzar.
                                            </div>
                                        )}

                                        <div className="flex justify-end">
                                            <Button
                                                onClick={handleSaveSubsection}
                                            >
                                                Guardar Cambios
                                            </Button>
                                        </div>
                                    </TabsContent>
                                </Tabs>
                            </CardContent>
                        </Card>
                    ) : (
                        <div className="flex flex-col items-center justify-center h-64 bg-muted/20 rounded-lg border border-dashed">
                            <div className="text-center p-6">
                                <h3 className="text-lg font-medium mb-2">
                                    Seleccione una subsección
                                </h3>
                                <p className="text-muted-foreground">
                                    Seleccione una subsección del menú de la
                                    izquierda para editar su contenido.
                                </p>
                            </div>
                        </div>
                    )}
                </div>
            </div>

            {/* Diálogo para crear/editar sección */}
            <Dialog
                open={isSectionDialogOpen}
                onOpenChange={setIsSectionDialogOpen}
            >
                <DialogContent className="sm:max-w-[425px]">
                    <DialogHeader>
                        <DialogTitle>
                            {sectionForm.id
                                ? "Editar Sección"
                                : "Nueva Sección"}
                        </DialogTitle>
                        <DialogDescription>
                            Complete los detalles de la sección. Haga clic en
                            guardar cuando termine.
                        </DialogDescription>
                    </DialogHeader>
                    <div className="grid gap-4 py-4">
                        <div className="grid gap-2">
                            <Label htmlFor="section-title">
                                Título de la sección
                            </Label>
                            <Input
                                id="section-title"
                                value={sectionForm.title}
                                onChange={(e) =>
                                    setSectionForm({
                                        ...sectionForm,
                                        title: e.target.value,
                                    })
                                }
                                placeholder="Ej: Introducción al curso"
                            />
                        </div>
                    </div>
                    <DialogFooter>
                        <Button
                            variant="outline"
                            onClick={() => setIsSectionDialogOpen(false)}
                        >
                            Cancelar
                        </Button>
                        <Button onClick={handleSaveSection}>Guardar</Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>

            {/* Diálogo para crear/editar subsección */}
            <Dialog
                open={isSubsectionDialogOpen}
                onOpenChange={setIsSubsectionDialogOpen}
            >
                <DialogContent className="sm:max-w-[550px]">
                    <DialogHeader>
                        <DialogTitle>
                            {subsectionForm.id
                                ? "Editar Subsección"
                                : "Nueva Subsección"}
                        </DialogTitle>
                        <DialogDescription>
                            Complete los detalles de la subsección. Haga clic en
                            guardar cuando termine.
                        </DialogDescription>
                    </DialogHeader>
                    <div className="grid gap-4 py-4">
                        <div className="grid gap-2">
                            <Label htmlFor="subsection-title-dialog">
                                Título de la subsección
                            </Label>
                            <Input
                                id="subsection-title-dialog"
                                value={subsectionForm.title}
                                onChange={(e) =>
                                    setSubsectionForm({
                                        ...subsectionForm,
                                        title: e.target.value,
                                    })
                                }
                                placeholder="Ej: Introducción a los conceptos básicos"
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="subsection-content-dialog">
                                Contenido
                            </Label>
                            <Textarea
                                id="subsection-content-dialog"
                                value={subsectionForm.content.body}
                                onChange={(e) =>
                                    setSubsectionForm({
                                        ...subsectionForm,
                                        content: {
                                            ...subsectionForm.content,
                                            body: e.target.value,
                                        },
                                    })
                                }
                                placeholder="Contenido HTML de la subsección"
                                rows={6}
                            />
                        </div>
                    </div>
                    <DialogFooter>
                        <Button
                            variant="outline"
                            onClick={() => setIsSubsectionDialogOpen(false)}
                        >
                            Cancelar
                        </Button>
                        <Button onClick={handleSaveSubsection}>Guardar</Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>

            {/* Diálogo para crear/editar enlace */}
            <Dialog open={isLinkDialogOpen} onOpenChange={setIsLinkDialogOpen}>
                <DialogContent className="sm:max-w-[425px]">
                    <DialogHeader>
                        <DialogTitle>
                            {linkForm.id ? "Editar Enlace" : "Nuevo Enlace"}
                        </DialogTitle>
                        <DialogDescription>
                            Agregue un enlace a recursos externos.
                        </DialogDescription>
                    </DialogHeader>
                    <div className="grid gap-4 py-4">
                        <div className="grid gap-2">
                            <Label htmlFor="link-title">
                                Título del enlace
                            </Label>
                            <Input
                                id="link-title"
                                value={linkForm.title}
                                onChange={(e) =>
                                    setLinkForm({
                                        ...linkForm,
                                        title: e.target.value,
                                    })
                                }
                                placeholder="Ej: Material de apoyo"
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="link-url">URL</Label>
                            <Input
                                id="link-url"
                                value={linkForm.url}
                                onChange={(e) =>
                                    setLinkForm({
                                        ...linkForm,
                                        url: e.target.value,
                                    })
                                }
                                placeholder="https://example.com/recurso.pdf"
                            />
                        </div>
                    </div>
                    <DialogFooter>
                        <Button
                            variant="outline"
                            onClick={() => setIsLinkDialogOpen(false)}
                        >
                            Cancelar
                        </Button>
                        <Button onClick={handleSaveLink}>Guardar</Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>

            {/* Diálogo de confirmación para eliminar */}
            <Dialog
                open={isDeleteDialogOpen}
                onOpenChange={setIsDeleteDialogOpen}
            >
                <DialogContent className="sm:max-w-[425px]">
                    <DialogHeader>
                        <DialogTitle>Confirmar eliminación</DialogTitle>
                        <DialogDescription>
                            ¿Está seguro de que desea eliminar este elemento?
                            Esta acción no se puede deshacer.
                        </DialogDescription>
                    </DialogHeader>
                    <DialogFooter className="flex space-x-2 justify-end">
                        <Button
                            variant="outline"
                            onClick={() => setIsDeleteDialogOpen(false)}
                        >
                            Cancelar
                        </Button>
                        <Button variant="destructive" onClick={handleDelete}>
                            Eliminar
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    );
}

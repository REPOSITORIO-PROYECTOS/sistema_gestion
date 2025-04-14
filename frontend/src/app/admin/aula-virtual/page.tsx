"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import {
    ArrowLeft,
    Plus,
    Pencil,
    Trash2,
    ChevronDown,
    BookOpen,
    Bell,
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
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Checkbox } from "@/components/ui/checkbox";
import { Label } from "@/components/ui/label";
import { Switch } from "@/components/ui/switch";
// import { useAuth } from "@/components/auth-provider"

// Tipos
type News = {
    id: string;
    title: string;
    content: string;
    date: string;
    category: string;
    important: boolean;
};

type Course = {
    id: string;
    title: string;
    description: string;
    status: "ACTIVE" | "PENDING" | "COMPLETED";
    recommended: boolean;
    price: number;
};

export default function Page() {
    //   const { user, isLoading } = useAuth()
    //   const router = useRouter()
    const [isClient, setIsClient] = useState(false);

    // Estado para noticias
    const [news, setNews] = useState<News[]>([
        {
            id: "1",
            title: "Nuevos cursos disponibles",
            content:
                "Hemos agregado 5 nuevos cursos a nuestra plataforma. ¡Explora las nuevas opciones en programación, diseño y marketing digital!",
            date: "2025-03-22",
            category: "Plataforma",
            important: true,
        }
    ]);

    // Estado para cursos
    const [courses, setCourses] = useState<Course[]>([
        {
            id: "1",
            title: "Inteligencia Artificial",
            description: "Fundamentos y aplicaciones prácticas",
            status: "ACTIVE",
            recommended: true,
            price: 8500,
        }
    ]);

    // Estado para formularios
    const [newsForm, setNewsForm] = useState<Omit<News, "id">>({
        title: "",
        content: "",
        date: new Date().toISOString().split("T")[0],
        category: "Plataforma",
        important: false,
    });

    const [editingNewsId, setEditingNewsId] = useState<string | null>(null);
    const [isNewsDialogOpen, setIsNewsDialogOpen] = useState(false);
    const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
    const [itemToDelete, setItemToDelete] = useState<{
        id: string;
        type: "news" | "course";
    } | null>(null);

    // Resetear formulario de noticias
    const resetNewsForm = () => {
        setNewsForm({
            title: "",
            content: "",
            date: new Date().toISOString().split("T")[0],
            category: "Plataforma",
            important: false,
        });
        setEditingNewsId(null);
    };

    // Abrir formulario para editar noticia
    const handleEditNews = (newsItem: News) => {
        setNewsForm({
            title: newsItem.title,
            content: newsItem.content,
            date: newsItem.date,
            category: newsItem.category,
            important: newsItem.important,
        });
        setEditingNewsId(newsItem.id);
        setIsNewsDialogOpen(true);
    };

    // Guardar noticia (nueva o editada)
    const handleSaveNews = () => {
        if (editingNewsId) {
            // Editar existente
            setNews(
                news.map((item) =>
                    item.id === editingNewsId
                        ? { ...newsForm, id: editingNewsId }
                        : item
                )
            );
        } else {
            // Crear nueva
            const newId = Date.now().toString();
            setNews([...news, { ...newsForm, id: newId }]);
        }

        setIsNewsDialogOpen(false);
        resetNewsForm();
    };

    // Confirmar eliminación
    const confirmDelete = () => {
        if (!itemToDelete) return;

        if (itemToDelete.type === "news") {
            setNews(news.filter((item) => item.id !== itemToDelete.id));
        } else {
            // No eliminamos cursos, solo quitamos la recomendación
            setCourses(
                courses.map((course) =>
                    course.id === itemToDelete.id
                        ? { ...course, recommended: false }
                        : course
                )
            );
        }

        setIsDeleteDialogOpen(false);
        setItemToDelete(null);
    };

    // Cambiar estado de recomendación de curso
    const toggleCourseRecommendation = (courseId: string) => {
        setCourses(
            courses.map((course) =>
                course.id === courseId
                    ? { ...course, recommended: !course.recommended }
                    : course
            )
        );
    };

    // Formatear fecha para mostrar
    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleDateString("es-ES", {
            day: "numeric",
            month: "long",
            year: "numeric",
        });
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
                        <Link href="/admin">
                            <ArrowLeft className="h-4 w-4" />
                        </Link>
                    </Button>
                    <h1 className="text-2xl font-bold tracking-tight">
                        Gestión de Contenido
                    </h1>
                </div>
            </div>

            <Tabs defaultValue="news" className="w-full">
                <TabsList className="mb-4">
                    <TabsTrigger value="news" className="flex items-center">
                        <Bell className="mr-2 h-4 w-4" />
                        Noticias y Anuncios
                    </TabsTrigger>
                    <TabsTrigger value="courses" className="flex items-center">
                        <BookOpen className="mr-2 h-4 w-4" />
                        Cursos Recomendados
                    </TabsTrigger>
                </TabsList>

                {/* Pestaña de Noticias */}
                <TabsContent value="news">
                    <div className="mb-4 flex justify-between items-center">
                        <h2 className="text-xl font-semibold">
                            Noticias y Anuncios
                        </h2>
                        <Dialog
                            open={isNewsDialogOpen}
                            onOpenChange={setIsNewsDialogOpen}
                        >
                            <DialogTrigger asChild>
                                <Button onClick={resetNewsForm}>
                                    <Plus className="mr-2 h-4 w-4" />
                                    Nueva Noticia
                                </Button>
                            </DialogTrigger>
                            <DialogContent className="sm:max-w-[550px]">
                                <DialogHeader>
                                    <DialogTitle>
                                        {editingNewsId
                                            ? "Editar Noticia"
                                            : "Nueva Noticia"}
                                    </DialogTitle>
                                    <DialogDescription>
                                        Complete los detalles de la noticia.
                                        Haga clic en guardar cuando termine.
                                    </DialogDescription>
                                </DialogHeader>
                                <div className="grid gap-4 py-4">
                                    <div className="grid gap-2">
                                        <Label htmlFor="title">Título</Label>
                                        <Input
                                            id="title"
                                            value={newsForm.title}
                                            onChange={(e) =>
                                                setNewsForm({
                                                    ...newsForm,
                                                    title: e.target.value,
                                                })
                                            }
                                            placeholder="Título de la noticia"
                                        />
                                    </div>
                                    <div className="grid gap-2">
                                        <Label htmlFor="content">
                                            Contenido
                                        </Label>
                                        <Textarea
                                            id="content"
                                            value={newsForm.content}
                                            onChange={(e) =>
                                                setNewsForm({
                                                    ...newsForm,
                                                    content: e.target.value,
                                                })
                                            }
                                            placeholder="Contenido de la noticia"
                                            rows={4}
                                        />
                                    </div>
                                    <div className="grid grid-cols-2 gap-4">
                                        <div className="grid gap-2">
                                            <Label htmlFor="date">Fecha</Label>
                                            <Input
                                                id="date"
                                                type="date"
                                                value={newsForm.date}
                                                onChange={(e) =>
                                                    setNewsForm({
                                                        ...newsForm,
                                                        date: e.target.value,
                                                    })
                                                }
                                            />
                                        </div>
                                        <div className="grid gap-2">
                                            <Label htmlFor="category">
                                                Categoría
                                            </Label>
                                            <DropdownMenu>
                                                <DropdownMenuTrigger asChild>
                                                    <Button
                                                        variant="outline"
                                                        className="w-full justify-between"
                                                    >
                                                        {newsForm.category}
                                                        <ChevronDown className="h-4 w-4 opacity-50" />
                                                    </Button>
                                                </DropdownMenuTrigger>
                                                <DropdownMenuContent>
                                                    <DropdownMenuItem
                                                        onClick={() =>
                                                            setNewsForm({
                                                                ...newsForm,
                                                                category:
                                                                    "Plataforma",
                                                            })
                                                        }
                                                    >
                                                        Plataforma
                                                    </DropdownMenuItem>
                                                    <DropdownMenuItem
                                                        onClick={() =>
                                                            setNewsForm({
                                                                ...newsForm,
                                                                category:
                                                                    "Técnico",
                                                            })
                                                        }
                                                    >
                                                        Técnico
                                                    </DropdownMenuItem>
                                                    <DropdownMenuItem
                                                        onClick={() =>
                                                            setNewsForm({
                                                                ...newsForm,
                                                                category:
                                                                    "Eventos",
                                                            })
                                                        }
                                                    >
                                                        Eventos
                                                    </DropdownMenuItem>
                                                    <DropdownMenuItem
                                                        onClick={() =>
                                                            setNewsForm({
                                                                ...newsForm,
                                                                category:
                                                                    "Académico",
                                                            })
                                                        }
                                                    >
                                                        Académico
                                                    </DropdownMenuItem>
                                                </DropdownMenuContent>
                                            </DropdownMenu>
                                        </div>
                                    </div>
                                    <div className="flex items-center space-x-2">
                                        <Checkbox
                                            id="important"
                                            checked={newsForm.important}
                                            onCheckedChange={(checked) =>
                                                setNewsForm({
                                                    ...newsForm,
                                                    important: checked === true,
                                                })
                                            }
                                        />
                                        <Label htmlFor="important">
                                            Marcar como importante
                                        </Label>
                                    </div>
                                </div>
                                <DialogFooter>
                                    <Button
                                        variant="outline"
                                        onClick={() =>
                                            setIsNewsDialogOpen(false)
                                        }
                                    >
                                        Cancelar
                                    </Button>
                                    <Button onClick={handleSaveNews}>
                                        Guardar
                                    </Button>
                                </DialogFooter>
                            </DialogContent>
                        </Dialog>
                    </div>

                    <div className="space-y-4">
                        {news.map((item) => (
                            <Card
                                key={item.id}
                                className={
                                    item.important ? "border-blue-200" : ""
                                }
                            >
                                <CardHeader className="pb-2">
                                    <div className="flex justify-between items-start">
                                        <div>
                                            <CardTitle className="text-lg">
                                                {item.title}
                                            </CardTitle>
                                            <CardDescription>
                                                {formatDate(item.date)}
                                            </CardDescription>
                                        </div>
                                        <div className="flex items-center space-x-2">
                                            <Badge variant="outline">
                                                {item.category}
                                            </Badge>
                                            {item.important && (
                                                <Badge className="bg-blue-500">
                                                    Importante
                                                </Badge>
                                            )}
                                        </div>
                                    </div>
                                </CardHeader>
                                <CardContent>
                                    <p className="text-sm">{item.content}</p>
                                </CardContent>
                                <CardFooter className="flex justify-end space-x-2">
                                    <Button
                                        variant="outline"
                                        size="sm"
                                        onClick={() => handleEditNews(item)}
                                    >
                                        <Pencil className="h-4 w-4 mr-1" />
                                        Editar
                                    </Button>
                                    <Button
                                        variant="destructive"
                                        size="sm"
                                        onClick={() => {
                                            setItemToDelete({
                                                id: item.id,
                                                type: "news",
                                            });
                                            setIsDeleteDialogOpen(true);
                                        }}
                                    >
                                        <Trash2 className="h-4 w-4 mr-1" />
                                        Eliminar
                                    </Button>
                                </CardFooter>
                            </Card>
                        ))}

                        {news.length === 0 && (
                            <div className="text-center py-8 text-muted-foreground">
                                No hay noticias disponibles. Haga clic en "Nueva
                                Noticia" para crear una.
                            </div>
                        )}
                    </div>
                </TabsContent>

                {/* Pestaña de Cursos Recomendados */}
                <TabsContent value="courses">
                    <div className="mb-4">
                        <h2 className="text-xl font-semibold">
                            Cursos Recomendados
                        </h2>
                        <p className="text-sm text-muted-foreground mt-1">
                            Seleccione los cursos que desea mostrar en la
                            sección de "Cursos Recomendados" de la página
                            principal.
                        </p>
                    </div>

                    <div className="space-y-4">
                        {courses.map((course) => (
                            <Card
                                key={course.id}
                                className={
                                    course.recommended ? "border-green-200" : ""
                                }
                            >
                                <CardHeader className="pb-2">
                                    <div className="flex justify-between items-start">
                                        <div>
                                            <CardTitle className="text-lg">
                                                {course.title}
                                            </CardTitle>
                                            <CardDescription>
                                                {course.description}
                                            </CardDescription>
                                        </div>
                                        <Badge
                                            variant={
                                                course.status === "ACTIVE"
                                                    ? "default"
                                                    : "outline"
                                            }
                                        >
                                            {course.status === "ACTIVE"
                                                ? "Activo"
                                                : "Pendiente"}
                                        </Badge>
                                    </div>
                                </CardHeader>
                                <CardContent>
                                    <div className="flex justify-between items-center">
                                        <div className="text-sm">
                                            <span className="font-medium">
                                                Precio:
                                            </span>{" "}
                                            $
                                            {course.price.toLocaleString(
                                                "es-ES"
                                            )}
                                        </div>
                                        <div className="flex items-center space-x-2">
                                            <Label
                                                htmlFor={`recommended-${course.id}`}
                                                className="text-sm"
                                            >
                                                Mostrar como recomendado
                                            </Label>
                                            <Switch
                                                id={`recommended-${course.id}`}
                                                checked={course.recommended}
                                                onCheckedChange={() =>
                                                    toggleCourseRecommendation(
                                                        course.id
                                                    )
                                                }
                                                disabled={
                                                    course.status !== "ACTIVE"
                                                }
                                            />
                                        </div>
                                    </div>
                                </CardContent>
                            </Card>
                        ))}
                    </div>
                </TabsContent>
            </Tabs>

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
                        <Button variant="destructive" onClick={confirmDelete}>
                            Eliminar
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    );
}

"use client";

import { useState, useEffect} from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import {
    ArrowLeft,
    Pencil,
    Trash2,
    ChevronDown,
    BookOpen,
    Bell,
    FileText,
    ImageIcon,
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
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from "@/components/ui/accordion";
import { GestionarNoticias } from "@/components/interfaces/gestionar-noticias";
import { EditarCursos } from "@/components/interfaces/gestionar-editar-cursos";
import { Evaluaciones } from "@/components/interfaces/agregar-resultado-examen";
import { useAuthStore } from "@/context/store";
// import { useAuth } from "@/components/auth-provider"

export default function Page() {
    //   const { user, isLoading } = useAuth()
    const router = useRouter()
    const [isClient, setIsClient] = useState(false);
    const {user} = useAuthStore();
    const [courses, setCourses] = useState<any>([]);
    const [sections, setSections] = useState<any>({});
    const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
    const [itemToDelete, setItemToDelete] = useState<{
        id: string;
        type: "news" | "course" | "subsection" | "file" | "image" | "section";
        sectionId?: string;
        subsectionId?: string;
    } | null>(null);
    //   if (!isClient || isLoading) {
    //     return <div className="flex h-screen items-center justify-center">Cargando...</div>
    //   }

    const confirmDelete = () => {
        if (!itemToDelete) return;

        if (itemToDelete.type === "news") {
            //setNewsState(news.filter((item:any) => item.id !== itemToDelete.id));
        }

        setIsDeleteDialogOpen(false);
        setItemToDelete(null);
    };

    return (
        <div className="container mx-auto py-6 px-4">
            {/* Cabecera */}
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

            {/* Pestañas */}
            <Tabs defaultValue="news" className="w-full">
                <TabsList className="mb-4 grid w-full grid-cols-1"> {/* Ajustado para 3 pestañas */}
                    <TabsTrigger value="news" className="flex items-center">
                        <Bell className="mr-2 h-4 w-4" />
                        Noticias y anuncios
                    </TabsTrigger>
                    {/* Nueva Pestaña */}
                    {/* <TabsTrigger value="edit-courses" className="flex items-center">
                        <Pencil className="mr-2 h-4 w-4" />
                        Editar material de estudio
                    </TabsTrigger>
                    <TabsTrigger value="evaluaciones" className="flex items-center">
                        <BookOpen className="mr-2 h-4 w-4" />
                        Evaluaciones
                    </TabsTrigger> */}
                </TabsList>

                {/* Pestaña de Noticias (sin cambios) */}
                <TabsContent value="news">
                    <GestionarNoticias news={[]} Dialog={Dialog} DialogTrigger={DialogTrigger} Button={Button} DialogContent={DialogContent}
                        DialogHeader={DialogHeader} DialogTitle={DialogTitle} DialogDescription={DialogDescription} DialogFooter={DialogFooter} Label={Label} Input={Input} Textarea={Textarea} DropdownMenu={DropdownMenu}
                        DropdownMenuTrigger={DropdownMenuTrigger} DropdownMenuContent={DropdownMenuContent} DropdownMenuItem={DropdownMenuItem} Checkbox={Checkbox} Card={Card} CardHeader={CardHeader} CardContent={CardContent}
                        CardFooter={CardFooter} Trash2={Trash2} Pencil={Pencil} ChevronDown={ChevronDown}
                    />
                </TabsContent>

                {/* Nueva Pestaña: Editar Cursos */}
                <TabsContent value="edit-courses">
                    <EditarCursos Card={Card} CardHeader={CardHeader} CardTitle={CardTitle} 
                    CardDescription={CardDescription} CardContent={CardContent} CardFooter={CardFooter} 
                    Button={Button} Trash2={Trash2} Label={Label} Input={Input} Textarea={Textarea} 
                    Accordion={Accordion} AccordionItem={AccordionItem} AccordionTrigger={AccordionTrigger} 
                    AccordionContent={AccordionContent} FileText={FileText} ImageIcon={ImageIcon}/>
                </TabsContent>

                <TabsContent value="evaluaciones">
                    <Evaluaciones exams={[]} onAddExam={{}} />
                </TabsContent>
            </Tabs>

            {/* Diálogo de confirmación para eliminar (Reutilizado) */}
            <Dialog
                open={isDeleteDialogOpen}
                onOpenChange={setIsDeleteDialogOpen}
            >
                <DialogContent className="sm:max-w-[425px]">
                    <DialogHeader>
                        <DialogTitle>Confirmar eliminación</DialogTitle>
                        <DialogDescription>
                            ¿Está seguro de que desea eliminar este elemento ({itemToDelete?.type}: {itemToDelete?.id || itemToDelete?.subsectionId})?
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

import { Plus } from "lucide-react";
import { useState } from "react";

type News = {
    id: string;
    title: string;
    content: string;
    date: string;
    category: string;
    important: boolean;
};

export const GestionarNoticias = ({ news, setItemToDelete, setIsDeleteDialogOpen, Dialog, DialogTrigger, Button, DialogContent,
    DialogHeader, DialogTitle, DialogDescription, DialogFooter, Label, Input, Textarea, DropdownMenu,
    DropdownMenuTrigger, DropdownMenuContent, DropdownMenuItem, Checkbox, Card, CardHeader, CardContent,
    CardFooter, Trash2, Pencil, ChevronDown, 
 }:any) => {
    const [editingNewsId, setEditingNewsId] = useState<string | null>(null);
    const [isNewsDialogOpen, setIsNewsDialogOpen] = useState(false);
    const [isLoading, setIsLoading] = useState<any>(false);
    const [newsState, setNewsState] = useState<News[]>([
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

    const [newsForm, setNewsForm] = useState<Omit<News, "id">>({
            title: "",
            content: "",
            date: new Date().toISOString().split("T")[0],
            category: "Plataforma",
            important: false,
        });
    
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
                setNewsState(
                    news.map((item:any) =>
                        item.id === editingNewsId
                            ? { ...newsForm, id: editingNewsId }
                            : item
                    )
                );
            } else {
                // Crear nueva
                const newId = Date.now().toString();
                setNewsState([...news, { ...newsForm, id: newId }]);
            }
    
            setIsNewsDialogOpen(false);
            resetNewsForm();
        };

        const formatDate = (dateString: string) => {
            const date = new Date(dateString);
            return date.toLocaleDateString("es-ES", {
                day: "numeric",
                month: "long",
                year: "numeric",
            });
        };

        return(
            <>
                    {/* ... (contenido existente de la pestaña de noticias) ... */}
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
                                {/* ... (contenido del diálogo de noticias) ... */}
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
                                            onChange={(e:any) =>
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
                                            onChange={(e:any) =>
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
                                                onChange={(e:any) =>
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
                                                    {/* ... otras categorías ... */}
                                                </DropdownMenuContent>
                                            </DropdownMenu>
                                        </div>
                                    </div>
                                    <div className="flex items-center space-x-2">
                                        <Checkbox
                                            id="important"
                                            checked={newsForm.important}
                                            onCheckedChange={(checked:any) =>
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
                        {/* ... (mapeo y renderizado de noticias) ... */}
                         {news.map((item:any) => (
                            <Card
                                key={item.id}
                                className={
                                    item.important ? "border-blue-200" : ""
                                }
                            >
                                <CardHeader className="pb-2">
                                   {/* ... */}
                                </CardHeader>
                                <CardContent>
                                    {/* ... */}
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
                    </div>
                </>
        )
}
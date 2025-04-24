import { Loader2, Newspaper, Package, PackageOpen, Pencil, Plus, Save, X } from "lucide-react";
import { useCallback, useEffect, useState } from "react";
import {
    fetchCursosApi, updateCursoApi, deleteCursoApi,
    createSectionApi, fetchSectionByIdApi, updateSectionApi, deleteSectionApi,
    createSubSectionApi, fetchSubSectionByIdApi, updateSubSectionApi, deleteSubSectionApi, addFileToSubSectionApi,
    uploadFileApi, fetchFilesApi, deleteFileApi
} from '@/lib/apiEndpoints';
import { toastMessage as toast } from "@/components/ui/toast";

// Interfaz básica para tipos (mejora la legibilidad sobre 'any')
interface Curso { id: string; title: string; description: string; sectionIds: string[]; [key: string]: any; }
interface Section { id: string; name: string; description: string; courseId: string; subSectionsIds: string[]; isNew?: boolean; [key: string]: any; }
interface SubSection { id: string; body: string; sectionId: string; filesIds: string[]; imagesIds: string[]; isNew?: boolean; [key: string]: any; }
interface FileData { id: string; name: string; url: string; [key: string]: any; }
interface EditingCursoData extends Curso { sections: (Section & { subSections: SubSection[] })[]; }

const initialCursos:any = [
    { id: "c1", title: "Introducción a React", description: "...", status: "ACTIVE", price: 50, recommended: false, sectionIds: ["s1-r", "s2-r"] },
    { id: "c2", title: "Node.js Avanzado", description: "...", status: "ACTIVE", price: 75, recommended: true, sectionIds: ["s3-n"] },
];
const initialSections = [
    { id: "s1-r", name: "Módulo 1: Fundamentos", description: "...", createdAt: "...", updatedAt: "...", courseId: "c1", subSectionsIds: ["sub1-jsx", "sub2-comp"] },
    { id: "s2-r", name: "Módulo 2: Hooks", description: "...", createdAt: "...", updatedAt: "...", courseId: "c1", subSectionsIds: ["sub3-state"] },
    { id: "s3-n", name: "Sección Única: Node Core", description: "...", createdAt: "...", updatedAt: "...", courseId: "c2", subSectionsIds: ["sub4-streams", "sub5-fs"] },
];
const initialSubSections = [
    { id: "sub1-jsx", body: "...", sectionId: "s1-r", filesIds: ["f1"], imagesIds: [] },
    { id: "sub2-comp", body: "...", sectionId: "s1-r", filesIds: [], imagesIds: ["img1"] },
    { id: "sub3-state", body: "...", sectionId: "s2-r", filesIds: [], imagesIds: [] },
    { id: "sub4-streams", body: "...", sectionId: "s3-n", filesIds: ["f2"], imagesIds: [] },
    { id: "sub5-fs", body: "...", sectionId: "s3-n", filesIds: [], imagesIds: ["img2"] },
];

export const EditarCursos = ({ Card, CardHeader, CardTitle, CardDescription, CardContent, CardFooter, 
    Button, Trash2, Input, Textarea, Accordion, AccordionItem, AccordionTrigger, 
    AccordionContent
}:any) => {
    const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
    const [itemToDelete, setItemToDelete] = useState<{ type: string; id: string; [key: string]: any } | null>(null);

    const [cursos, setCursos] = useState<Curso[]>([]);
    const [sections, setSections] = useState<Record<string, Section>>({}); // Mapa global
    const [subSections, setSubSections] = useState<Record<string, SubSection>>({}); // Mapa global
    const [fileMetadata, setFileMetadata] = useState<Record<string, FileData>>({}); // Mapa global de archivos

    const [editingCursoId, setEditingCursoId] = useState<string | null>(null);
    const [currentCursoData, setCurrentCursoData] = useState<EditingCursoData | null>(null); // Datos en edición
    const [originalCursoData, setOriginalCursoData] = useState<EditingCursoData | null>(null); // Snapshot

    const [isLoading, setIsLoading] = useState(true); // Carga inicial y detalles
    const [isSaving, setIsSaving] = useState(false);  // Guardado general
    const [isDeleting, setIsDeleting] = useState(false); // Borrado (diálogo)
    const [isUploading, setIsUploading] = useState(false); // Subida de archivos

    // --- Carga Inicial ---
    useEffect(() => {
        const loadInitialData = async () => {
            setIsLoading(true);
            try {
                // Paralelizar fetch inicial
                const [cursosRes, filesRes] = await Promise.all([
                    fetchCursosApi(),
                    fetchFilesApi()
                ]);
                setCursos(cursosRes || []); // Asegurar que sea un array
                const filesMap = (filesRes || []).reduce((acc: Record<string, FileData>, file:FileData) => {
                    acc[file.id] = file;
                    return acc;
                }, {});
                setFileMetadata(filesMap);
            } catch (error: any) {
                console.error("Error loading initial data:", error);
                toast({ variant: "destructive", title: "Error al cargar datos iniciales", description: error.message });
            } finally {
                setIsLoading(false);
            }
        };
        loadInitialData();
    }, [toast]); // Dependencia estable

    // --- Cargar Detalles para Editar ---
    const handleEditCurso = useCallback(async (cursoId: string) => {
        const cursoBase = cursos.find((c) => c.id === cursoId);
        if (!cursoBase) {
            toast({ variant: "destructive", title: "Error", description: "Curso no encontrado localmente." });
            return;
        };

        setIsLoading(true);
        try {
            // Obtener secciones del curso
            const fetchedSections: Section[] = await Promise.all(
                (cursoBase.sectionIds || []).map(id => fetchSectionByIdApi(id).catch(e => {
                     console.warn(`Sección ${id} no encontrada o error:`, e); return null; // Manejar error por sección
                }))
            ).then(results => results.filter((s): s is Section => s !== null)); // Filtrar nulos y asegurar tipo

            // Actualizar mapa global de secciones
            const sectionsMapUpdate = fetchedSections.reduce((acc: Record<string, Section>, sec) => {
                acc[sec.id] = sec; return acc;
            }, {});
            setSections(prev => ({ ...prev, ...sectionsMapUpdate }));

            // Obtener subsecciones de las secciones encontradas
            const subSectionFetchPromises = fetchedSections.flatMap(sec =>
                (sec.subSectionsIds || []).map(subId => fetchSubSectionByIdApi(subId).catch(e => {
                    console.warn(`Subsección ${subId} no encontrada o error:`, e); return null;
                }))
            );
            const fetchedSubSections: SubSection[] = await Promise.all(subSectionFetchPromises)
                .then(results => results.filter((s): s is SubSection => s !== null));

            // Actualizar mapa global de subsecciones
            const subSectionsMapUpdate = fetchedSubSections.reduce((acc: Record<string, SubSection>, sub) => {
                acc[sub.id] = sub; return acc;
            }, {});
            setSubSections(prev => ({ ...prev, ...subSectionsMapUpdate }));

            // Construir estructura anidada para edición
            const cursoSectionsNested = fetchedSections.map(section => ({
                ...section, // Datos de la sección
                subSections: (section.subSectionsIds || [])
                    .map(subId => subSectionsMapUpdate[subId] || subSections[subId]) // Usar datos frescos o los previos si falló algo
                    .filter((s): s is SubSection => s !== null), // Filtrar nulos y asegurar tipo
            }));

            const cursoDataParaEditar: EditingCursoData = { ...cursoBase, sections: cursoSectionsNested };
            setCurrentCursoData(cursoDataParaEditar);
            setOriginalCursoData(JSON.parse(JSON.stringify(cursoDataParaEditar))); // Snapshot
            setEditingCursoId(cursoId);

        } catch (error: any) {
            console.error(`Error loading details for course ${cursoId}:`, error);
            toast({ variant: "destructive", title: "Error al cargar detalles", description: error.message });
        } finally {
            setIsLoading(false);
        }
    }, [cursos, subSections, toast]); // Depende de cursos, subSections (para fallback) y toast

    // --- Cancelar Edición ---
    const handleCancelEditCurso = () => {
        setEditingCursoId(null);
        setCurrentCursoData(null);
        setOriginalCursoData(null); // Limpiar snapshot
    };

    // --- Cambios Locales (en estado de edición) ---
    const handleCursoChange = (field: keyof Curso, value: any) => {
        setCurrentCursoData(prev => prev ? { ...prev, [field]: value } : null);
    };

    const handleSectionChange = (sectionId: string, field: keyof Section, value: any) => {
        setCurrentCursoData(prev => {
            if (!prev) return null;
            const updatedSections = prev.sections.map(sec =>
                sec.id === sectionId ? { ...sec, [field]: value } : sec
            );
            return { ...prev, sections: updatedSections };
        });
    };

    const handleSubSectionChange = (sectionId: string, subSectionId: string, field: keyof SubSection, value: any) => {
        setCurrentCursoData(prev => {
            if (!prev) return null;
            const updatedSections = prev.sections.map(sec => {
                if (sec.id === sectionId) {
                    const updatedSubSections = sec.subSections.map(sub =>
                        sub.id === subSectionId ? { ...sub, [field]: value } : sub
                    );
                    return { ...sec, subSections: updatedSubSections };
                }
                return sec;
            });
            return { ...prev, sections: updatedSections };
        });
    };

    // --- Añadir Sección (Localmente) ---
    const handleAddSection = () => {
        if (!editingCursoId) return;
        const newSectionId = `s-local-${Date.now()}`;
        const newSection: Section = {
            id: newSectionId,
            name: "Nueva Sección (haz clic para editar)",
            description: "",
            courseId: editingCursoId,
            subSectionsIds: [],
            isNew: true, // Flag
            // createdAt/updatedAt se asignarán en backend
        };
        setCurrentCursoData(prev => prev ? ({
            ...prev,
            // sectionIds se actualizará al guardar
            sections: [...prev.sections, { ...newSection, subSections: [] }], // Añadir con subSections vacío
        }) : null);
    };

    // --- Añadir Subsección (Localmente) ---
    const handleAddSubSection = (sectionId: string) => {
        const newSubSectionId = `sub-local-${Date.now()}`;
        const newSubSection: SubSection = {
            id: newSubSectionId,
            body: "<p>Nuevo contenido...</p>",
            sectionId: sectionId,
            filesIds: [],
            imagesIds: [],
            isNew: true, // Flag
        };
        setCurrentCursoData(prev => {
            if (!prev) return null;
            const updatedSections = prev.sections.map(sec => {
                if (sec.id === sectionId) {
                    // Asegurarse de que subSections exista
                    const currentSubSections = sec.subSections || [];
                    return {
                        ...sec,
                        // subSectionsIds se actualizará al guardar
                        subSections: [...currentSubSections, newSubSection],
                    };
                }
                return sec;
            });
            return { ...prev, sections: updatedSections };
        });
    };

    // --- Disparar Diálogo de Confirmación de Borrado ---
    const handleDeleteRequest = (type: string, id: string, extraData: Record<string, any> = {}) => {
        setItemToDelete({ type, id, ...extraData });
        setIsDeleteDialogOpen(true);
    };

    // --- Guardado General con API ---
    const handleSaveCursoChanges = async () => {
        if (!currentCursoData || !originalCursoData) return;
        setIsSaving(true);

        // Clonar para no mutar estado directamente mientras se procesa
        let processedData = JSON.parse(JSON.stringify(currentCursoData));
        let finalSectionIdsOrder: string[] = []; // Para el orden final
        let changesMade = false; // Flag para saber si hubo cambios

        try {
            // 1. Actualizar Curso Base (si cambió)
            const cursoChanges: Partial<Curso> = {};
            if (processedData.title !== originalCursoData.title) cursoChanges.title = processedData.title;
            if (processedData.description !== originalCursoData.description) cursoChanges.description = processedData.description;
            if (Object.keys(cursoChanges).length > 0) {
                await updateCursoApi(processedData.id, cursoChanges);
                changesMade = true;
            }

            // 2. Identificar Secciones Eliminadas
            const currentSectionIds = new Set(processedData.sections.map((s: Section) => s.id));
            const originalSectionIds = new Set(originalCursoData.sections.map((s: Section) => s.id));
            const sectionsToDelete = originalCursoData.sections.filter(s => !currentSectionIds.has(s.id));
            if (sectionsToDelete.length > 0) {
                await Promise.all(sectionsToDelete.map(s => deleteSectionApi(s.id)));
                changesMade = true;
            }

            // 3. Procesar Secciones Actuales (Crear/Actualizar)
            for (let i = 0; i < processedData.sections.length; i++) {
                let section = processedData.sections[i];
                let originalSection = originalCursoData.sections.find(s => s.id === section.id);

                // --- Crear Nueva Sección ---
                if (section.isNew) {
                    const createData = { name: section.name, description: section.description, courseId: processedData.id };
                    const newApiSection = await createSectionApi(createData);
                    const realSectionId = newApiSection.id; // ID real del backend
                    processedData.sections[i].id = realSectionId; // Actualizar ID localmente
                    processedData.sections[i].isNew = false; // Quitar flag
                    section = processedData.sections[i]; // Referencia actualizada
                    originalSection = undefined; // No hay original para comparar subs
                    finalSectionIdsOrder.push(realSectionId); // Añadir al orden final
                    changesMade = true;

                    // Ahora procesa las subsecciones (que también deben ser nuevas)
                    await processSectionSubSections(processedData.id, realSectionId, section, []);

                }
                // --- Actualizar Sección Existente ---
                else if (originalSection) {
                    const sectionChanges: Partial<Section> = {};
                    if (section.name !== originalSection.name) sectionChanges.name = section.name;
                    if (section.description !== originalSection.description) sectionChanges.description = section.description;

                    if (Object.keys(sectionChanges).length > 0) {
                        await updateSectionApi(section.id, sectionChanges);
                        changesMade = true;
                    }
                    finalSectionIdsOrder.push(section.id); // Añadir al orden final

                    // Procesar subsecciones de esta sección existente
                    await processSectionSubSections(processedData.id, section.id, section, originalSection.subSections || []);
                } else {
                     // Sección existe localmente pero no en original? Caso raro, podría ser error o borrada mientras se editaba. Ignorar o manejar.
                     console.warn(`Sección local ${section.id} no encontrada en datos originales.`);
                }
            }

             // 4. Actualizar Orden de Secciones si cambió
            const orderChanged = JSON.stringify(finalSectionIdsOrder) !== JSON.stringify(originalCursoData.sectionIds);
            if (orderChanged && finalSectionIdsOrder.length > 0) {
                await updateCursoApi(processedData.id, { sectionIds: finalSectionIdsOrder });
                changesMade = true;
            }

            // 5. Éxito: Recargar datos y salir del modo edición
            toast({ title: "Curso guardado exitosamente" });
            await reloadCursoData(); // Recargar todo para consistencia
            handleCancelEditCurso();

        } catch (error: any) {
            console.error("Error saving course changes:", error);
            toast({ variant: "destructive", title: "Error al guardar el curso", description: error.message });
        } finally {
            setIsSaving(false);
        }
    };

    // --- Función Auxiliar para Procesar Subsecciones (dentro de save) ---
    const processSectionSubSections = async (
        cursoId: string,
        sectionId: string,
        currentSectionData: Section & { subSections: SubSection[] }, // Datos actuales de la sección
        originalSubSections: SubSection[] // Subs originales de esta sección
    ) => {
        let subChangesMade = false;
        // Clonar subs actuales para procesar
        let processedSubs = JSON.parse(JSON.stringify(currentSectionData.subSections || []));
        let finalSubSectionIdsOrder: string[] = []; // Para orden

        // 1. Identificar Subsecciones Eliminadas
        const currentSubIds = new Set(processedSubs.map((s: SubSection) => s.id));
        const originalSubIds = new Set(originalSubSections.map((s: SubSection) => s.id));
        const subsToDelete = originalSubSections.filter(s => !currentSubIds.has(s.id));
        if (subsToDelete.length > 0) {
            await Promise.all(subsToDelete.map(s => deleteSubSectionApi(s.id)));
            subChangesMade = true;
        }

        // 2. Procesar Subsecciones Actuales (Crear/Actualizar)
        for (let i = 0; i < processedSubs.length; i++) {
            let sub = processedSubs[i];
            let originalSub = originalSubSections.find(s => s.id === sub.id);

             // --- Crear Nueva Subsección ---
            if (sub.isNew) {
                const createData = { body: sub.body }; // Añadir más campos si existen
                const newApiSub = await createSubSectionApi(sectionId, createData); // Usar sectionId real
                const realSubId = newApiSub.id;
                processedSubs[i].id = realSubId; // Actualizar ID local
                processedSubs[i].isNew = false; // Quitar flag
                sub = processedSubs[i];
                originalSub = undefined;
                finalSubSectionIdsOrder.push(realSubId);
                subChangesMade = true;

                // Asociar archivos/imágenes iniciales a la nueva subsección
                const initialFiles = sub.filesIds || [];
                const initialImages = sub.imagesIds || [];
                await Promise.all(initialFiles.map((fileId: string) => addFileToSubSectionApi(realSubId, fileId)));
                await Promise.all(initialImages.map((imgId: string) => addFileToSubSectionApi(realSubId, imgId))); // Asume mismo endpoint

            }
            // --- Actualizar Subsección Existente ---
            else if (originalSub) {
                const subChanges: Partial<SubSection> = {};
                if (sub.body !== originalSub.body) subChanges.body = sub.body;

                // Comparar arrays de IDs de archivos/imágenes
                const filesChanged = JSON.stringify((sub.filesIds || []).sort()) !== JSON.stringify((originalSub.filesIds || []).sort());
                const imagesChanged = JSON.stringify((sub.imagesIds || []).sort()) !== JSON.stringify((originalSub.imagesIds || []).sort());

                if (Object.keys(subChanges).length > 0 || filesChanged || imagesChanged) {
                     const updateData = {
                        ...subChanges,
                        // **Importante**: Enviar las listas *actuales* de IDs
                        filesIds: sub.filesIds || [],
                        imagesIds: sub.imagesIds || [],
                    };
                    await updateSubSectionApi(sub.id, updateData);
                    subChangesMade = true;
                }
                 finalSubSectionIdsOrder.push(sub.id);
            } else {
                console.warn(`Subsección local ${sub.id} no encontrada en originales.`);
            }
        }

        // 3. Actualizar orden de Subsecciones en la Sección (si cambió y hubo cambios)
        // El backend podría manejar esto automáticamente al crear/borrar,
        // pero si hay un campo de orden o un endpoint específico, se usaría aquí.
        // Si no, al menos `subChangesMade` indica si algo cambió dentro de la sección.

        // Actualizar los datos de subsecciones en el objeto de sección procesado
        currentSectionData.subSections = processedSubs;
        currentSectionData.subSectionsIds = finalSubSectionIdsOrder;

        return subChangesMade; // Devolver si hubo cambios en esta sección
    };

    // --- Borrado Confirmado con API ---
    const confirmDelete = async () => {
        if (!itemToDelete) return;
        setIsDeleting(true);

        const { type, id, sectionId, cursoId, fileId, subSectionId } = itemToDelete;

        try {
            switch (type) {
                case 'curso':
                    await deleteCursoApi(id);
                    // Recargar lista principal es lo más seguro
                    await reloadCursoData();
                    if (editingCursoId === id) handleCancelEditCurso(); // Salir si se estaba editando
                    break;
                case 'section':
                    await deleteSectionApi(id);
                    // Eliminar del estado local y recargar detalles si se estaba editando
                     setSections(prev => { const next = { ...prev }; delete next[id]; return next; });
                     setCursos(prev => prev.map(c => c.id === cursoId ? { ...c, sectionIds: c.sectionIds.filter(sid => sid !== id) } : c));
                    if (editingCursoId === cursoId) {
                         // Recargar detalles del curso actual para reflejar sección eliminada
                         await handleEditCurso(cursoId);
                     }
                    break;
                case 'subsection':
                    await deleteSubSectionApi(id);
                     // Eliminar del estado local y recargar detalles si se estaba editando
                     setSubSections(prev => { const next = { ...prev }; delete next[id]; return next; });
                     setSections(prev => {
                        const sec = prev[sectionId];
                        if (sec) {
                            return { ...prev, [sectionId]: { ...sec, subSectionsIds: sec.subSectionsIds.filter(sid => sid !== id) } };
                        } return prev;
                     });
                    if (editingCursoId === cursoId) {
                        // Recargar detalles del curso actual
                        await handleEditCurso(cursoId);
                    }
                    break;
                case 'file': // Desasociar archivo (ocurre al guardar) - o borrar permanentemente
                     if (itemToDelete.permanentDelete && fileId) { // Si se añade lógica para borrado permanente
                         await deleteFileApi(fileId);
                         setFileMetadata(prev => { const next = {...prev}; delete next[fileId]; return next; });
                         // Quitar de TODOS los arrays locales donde aparezca (más complejo)
                     } else {
                          // Lógica local para quitarlo de currentCursoData (UI optimista)
                         setCurrentCursoData(prev => {
                            if (!prev) return null;
                            return {
                                ...prev,
                                sections: prev.sections.map(sec => ({
                                    ...sec,
                                    subSections: sec.subSections.map(sub => {
                                        if (sub.id === subSectionId) {
                                            return {
                                                ...sub,
                                                filesIds: (sub.filesIds || []).filter(fid => fid !== fileId),
                                                imagesIds: (sub.imagesIds || []).filter(imgId => imgId !== fileId),
                                            };
                                        }
                                        return sub;
                                    }),
                                })),
                            };
                        });
                     }
                    break;
                default:
                    throw new Error(`Tipo de eliminación desconocido: ${type}`);
            }
            toast({ title: `${type.charAt(0).toUpperCase() + type.slice(1)} eliminado/actualizado` });
        } catch (error: any) {
            console.error(`Error deleting ${type} ${id}:`, error);
            toast({ variant: "destructive", title: `Error al procesar ${type}`, description: error.message });
        } finally {
            setIsDeleting(false);
            setIsDeleteDialogOpen(false);
            setItemToDelete(null);
        }
    };

     // --- Recargar Datos del Curso (y globales si es necesario) ---
     const reloadCursoData = async () => {
        setIsLoading(true);
        try {
            const cursosRes = await fetchCursosApi();
            setCursos(cursosRes || []);
            // Opcionalmente, recargar archivos si pueden cambiar fuera de este componente
            // const filesRes = await fetchFilesApi();
            // setFileMetadata(...)
        } catch (error: any) {
            toast({ variant: "destructive", title: "Error recargando cursos", description: error.message });
        } finally {
            setIsLoading(false);
        }
     };

     // --- Función para Subir Archivo ---
     const handleFileUpload = async (event: React.ChangeEvent<HTMLInputElement>, sectionId: string, subSectionId: string) => {
        const file = event.target.files?.[0];
        if (!file || !subSectionId || subSectionId.startsWith('sub-local-')) {
             if (subSectionId.startsWith('sub-local-')) {
                 toast({ variant: "warning", title: "Guardar Cambios", description: "Guarda los cambios del curso antes de añadir archivos a nuevas subsecciones." });
             }
            event.target.value = ''; // Limpiar input
            return;
        }

        const formData = new FormData();
        formData.append('file', file);

        setIsUploading(true);
        try {
            const uploadedFileData = await uploadFileApi(formData);
            setFileMetadata(prev => ({ ...prev, [uploadedFileData.id]: uploadedFileData }));

            // Asociar inmediatamente
            await addFileToSubSectionApi(subSectionId, uploadedFileData.id);

            // Actualizar estado local optimista
            const isImage = file.type.startsWith('image/');
            setCurrentCursoData(prev => {
                if (!prev) return null;
                return {
                    ...prev,
                    sections: prev.sections.map(sec => {
                        if (sec.id === sectionId) {
                            return {
                                ...sec,
                                subSections: sec.subSections.map(sub => {
                                    if (sub.id === subSectionId) {
                                        const files = sub.filesIds || [];
                                        const images = sub.imagesIds || [];
                                        return {
                                            ...sub,
                                            filesIds: isImage ? files : [...files, uploadedFileData.id],
                                            imagesIds: isImage ? [...images, uploadedFileData.id] : images,
                                        };
                                    }
                                    return sub;
                                })
                            };
                        }
                        return sec;
                    })
                };
            });

            toast({ title: "Archivo subido y asociado" });

        } catch (error: any) {
            console.error("Error uploading file:", error);
            toast({ variant: "destructive", title: "Error al subir archivo", description: error.message });
        } finally {
            setIsUploading(false);
            event.target.value = ''; // Limpiar input
        }
    };

    // --- Renderizado ---
    return (
        <div className="container mx-auto py-6 px-4">
            {isLoading && <div className="text-center p-8"><Loader2 className="mx-auto h-8 w-8 animate-spin text-primary" /></div>}

{!isLoading && !editingCursoId && (
    // --- Vista Lista de Cursos ---
    <div>
        <h2 className="text-xl font-semibold mb-4">Selecciona un Curso para Editar</h2>
        {/* Opcional: Botón Crear Nuevo Curso */}
        <div className="space-y-4">
            {cursos.map((curso:any) => (
                <Card key={curso.id}>
                    {/* ... (Card de Curso como antes) ... */}
                    <CardHeader>
                        <CardTitle className="flex items-center justify-between">
                            <span className="flex items-center"><Package className="mr-2 h-5 w-5 opacity-70"/>{curso.title}</span>
                            
                        </CardTitle>
                        <CardDescription>{curso.description}</CardDescription>
                    </CardHeader>
                    <CardFooter className="flex justify-end space-x-2">
                         <Button variant="destructive" size="sm" onClick={() => {setItemToDelete({ id: curso.id, type: 'curso' }); setIsDeleteDialogOpen(true);}}>
                             <Trash2 className="mr-1 h-4 w-4" /> Eliminar
                         </Button>
                         <Button size="sm" onClick={() => handleEditCurso(curso.id)}>
                             <Pencil className="mr-1 h-4 w-4" /> Editar Contenido
                         </Button>
                    </CardFooter>
                </Card>
            ))}
            {cursos.length === 0 && <p className="text-muted-foreground">No hay cursos creados.</p>}
        </div>
    </div>
)}

{!isLoading && editingCursoId && currentCursoData && (
    // --- Vista Edición Curso Específico ---
    <div>
        {editingCursoId && currentCursoData && (
                     <Button onClick={handleSaveCursoChanges} disabled={isSaving}>
                        {isSaving ? <Loader2 className="mr-2 h-4 w-4 animate-spin" /> : <Save className="mr-2 h-4 w-4" />}
                        {isSaving ? "Guardando..." : "Guardar Cambios del Curso"}
                    </Button>
                 )}
        {/* Cabecera Edición (Botón Guardar movido arriba) */}
        <div className="flex justify-between items-center mb-6 pb-4 border-b">
            <h2 className="text-2xl font-semibold flex items-center">
                <Package className="h-6 w-6 mr-2 opacity-80"/>
                Editando: {currentCursoData.title}
            </h2>
            <Button variant="outline" onClick={handleCancelEditCurso} disabled={isSaving}>
                 <X className="h-4 w-4 mr-1" /> Cancelar Edición
            </Button>
        </div>

        {/* Detalles del Curso */}
        <Card className="mb-6 bg-blue-50 dark:bg-blue-900/20 border-blue-200 dark:border-blue-800">
            {/* ... (Inputs para title, description como antes) ... */}
            <CardHeader><CardTitle>Detalles del Curso</CardTitle></CardHeader>
            <CardContent className="space-y-4">
                 <Input value={currentCursoData.title} onChange={(e:any) => handleCursoChange('title', e.target.value)} placeholder="Título del Curso"/>
                 <Textarea value={currentCursoData.description} onChange={(e:any) => handleCursoChange('description', e.target.value)} placeholder="Descripción General" rows={3}/>
            </CardContent>
        </Card>

        {/* *** GESTIÓN DE SECCIONES (CON BOTÓN AÑADIR) *** */}
        <div className="mb-4 flex justify-between items-center">
            <h3 className="text-xl font-semibold">Secciones del Curso</h3>
            {/* ***** BOTÓN PARA AÑADIR SECCIÓN ***** */}
            <Button size="sm" onClick={handleAddSection} disabled={isSaving}>
                <Plus className="h-4 w-4 mr-1" /> Añadir Sección
            </Button>
        </div>

        {/* Acordeón para Secciones */}
        {currentCursoData.sections.length > 0 ? (
            <Accordion type="multiple" className="w-full space-y-3">
                {currentCursoData.sections.map((section:any, sectionIndex:any) => (
                    <AccordionItem key={section.id} value={section.id} className="border rounded-md bg-card shadow-sm">
                        <AccordionTrigger className="px-4 py-3 text-base font-medium hover:no-underline">
                            <span className="flex items-center"><PackageOpen className="mr-2 h-5 w-5 opacity-70"/>Sección {sectionIndex + 1}: {section.name}</span>
                        </AccordionTrigger>
                        <AccordionContent className="px-4 pt-2 pb-4 border-t">
                            {/* Formulario Edición Sección */}
                            <div className="space-y-4 mb-6 p-4 bg-secondary/30 rounded-md">
                                <Input
                                    value={section.name}
                                    onChange={(e:any) => handleSectionChange(section.id, 'name', e.target.value)}
                                    placeholder="Nombre de la sección"
                                    className="text-base font-semibold"
                                />
                                <Textarea
                                    value={section.description || ""}
                                    onChange={(e:any) => handleSectionChange(section.id, 'description', e.target.value)}
                                    placeholder="Descripción de la sección (opcional)"
                                    rows={2}
                                />
                                <div className="flex justify-end">
                                    <Button variant="destructive" size="sm" onClick={() => handleDeleteRequest("section", section.id)} disabled={isSaving}>
                                        <Trash2 className="h-4 w-4 mr-1" /> Eliminar Sección
                                    </Button>
                                </div>
                            </div>

                            {/* Gestión de Subsecciones */}
                            <div className="mb-3 flex justify-between items-center">
                                <h4 className="text-md font-semibold">Subsecciones</h4>
                                <Button variant="outline" size="sm" onClick={() => handleAddSubSection(section.id)} disabled={isSaving}>
                                    <Plus className="h-3 w-3 mr-1" /> Añadir Subsección
                                </Button>
                            </div>

                            {/* Lista/Acordeón de Subsecciones */}
                            {section.subSections.length > 0 ? (
                                 <div className="space-y-3">
                                    {section.subSections.map((sub:any, subIndex:any) => (
                                        <Card key={sub.id} className="bg-background">
                                            {/* ... (Contenido de la Card de Subsección como antes) ... */}
                                             <CardHeader className="py-3 px-4 border-b">
                                                <CardTitle className="text-sm font-medium flex justify-between items-center">
                                                    <span className="flex items-center"><Newspaper className="mr-2 h-4 w-4 opacity-60"/>Subsección {subIndex + 1}</span>
                                                    <Button variant="ghost" size="sm" className="text-red-500 hover:text-red-700 px-1 h-auto" onClick={() => handleDeleteRequest("subsection", sub.id)} disabled={isSaving}>
                                                        <Trash2 className="h-3 w-3"/>
                                                    </Button>
                                                </CardTitle>
                                            </CardHeader>
                                            <CardContent className="p-4 space-y-4">
                                                 <Textarea value={sub.body} onChange={(e:any) => handleSubSectionChange(section.id, sub.id, 'body', e.target.value)} placeholder="Contenido..." rows={6}/>
                                                 {/* Files & Images Placeholders */}
                                            </CardContent>
                                        </Card>
                                    ))}
                                </div>
                            ) : (
                                <div className="text-center py-4 text-sm text-muted-foreground border rounded-md">Esta sección no tiene subsecciones.</div>
                            )}
                        </AccordionContent>
                    </AccordionItem>
                ))}
            </Accordion>
        ) : (
             <div className="text-center py-8 text-muted-foreground border rounded-md">
                 Este curso aún no tiene secciones. Haz clic en "Añadir Sección".
             </div>
        )}
    </div>
)}
        </div>
    );
}



// const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
// const [itemToDelete, setItemToDelete] = useState<any>(null);

// const [cursos, setCursos] = useState<any>([]);
// const [sections, setSections] = useState<any>({});
// const [subSections, setSubSections] = useState<any>({});
// const [editingCursoId, setEditingCursoId] = useState<any>(null);
// const [currentCursoData, setCurrentCursoData] = useState<any>(null);
// const [isLoading, setIsLoading] = useState<any>(false);
// const [isSaving, setIsSaving] = useState<any>(false); // Estado específico para el guardado

// // --- Carga Inicial ---
// useEffect(() => {
//     setIsLoading(true);
//     // Simular fetch
//     setTimeout(() => {
//         setCursos(initialCursos);
//         const sectionsMap = initialSections.reduce((acc:any, sec) => { acc[sec.id] = sec; return acc; }, {});
//         setSections(sectionsMap);
//         const subSectionsMap = initialSubSections.reduce((acc:any, sub) => { acc[sub.id] = sub; return acc; }, {});
//         setSubSections(subSectionsMap);
//         setIsLoading(false);
//     }, 500);
// }, []);

// const handleEditCurso = useCallback((cursoId:any) => {
//     const curso:any = cursos.find((c:any) => c.id === cursoId);
//     if (!curso) return;
//     setIsLoading(true);
//     // Construir estructura anidada para edición
//     const cursoSections = curso.sectionIds
//         .map((secId:any) => sections[secId])
//         .filter(Boolean)
//         .map((section:any) => ({
//             ...section,
//             subSections: section.subSectionsIds
//                 .map((subId:any) => subSections[subId])
//                 .filter(Boolean)
//         }));
//     setCurrentCursoData({ ...curso, sections: cursoSections });
//     setEditingCursoId(cursoId);
//     setIsLoading(false);
// }, [cursos, sections, subSections]);

// const handleCancelEditCurso = () => {
//     setEditingCursoId(null);
//     setCurrentCursoData(null);
// };

// // Cambios en Curso
// const handleCursoChange = (field:any, value:any) => {
//     setCurrentCursoData((prev:any) => prev ? ({ ...prev, [field]: value }) : null);
// };

// // Cambios en Sección
// const handleSectionChange = (sectionId:any, field:any, value:any) => {
//     setCurrentCursoData((prev:any) => {
//         if (!prev) return null;
//         const updatedSections = prev.sections.map((sec:any) =>
//             sec.id === sectionId ? { ...sec, [field]: value } : sec
//         );
//         return { ...prev, sections: updatedSections };
//     });
// };

// const handleAddSection = () => {
//     if (!editingCursoId) return; // Seguridad

//     const newSectionId = `s-new-${Date.now()}`; // ID Temporal cliente
//     const newSection = {
//         id: newSectionId,
//         name: "Nueva Sección (haz clic para editar)", // Nombre por defecto
//         description: "",
//         courseId: editingCursoId,
//         subSectionsIds: [], // Inicia sin subsecciones
//         subSections: [],   // Array vacío para la edición local
//         createdAt: new Date().toISOString(), // Fecha temporal
//         updatedAt: new Date().toISOString(), // Fecha temporal
//         // Marcar como nueva para facilitar el guardado (opcional)
//         isNew: true,
//     };

//     setCurrentCursoData((prev:any) => {
//         if (!prev) return null;
//         return {
//             ...prev,
//             // Añadir ID a la lista principal del curso (importante para el guardado)
//             sectionIds: [...prev.sectionIds, newSectionId],
//             // Añadir el objeto sección completo a la lista anidada para edición inmediata
//             sections: [...prev.sections, newSection],
//         };
//     });

//     console.log("Nueva sección añadida al estado de edición:", newSection);
//     // Nota: La sección se guardará permanentemente al hacer clic en "Guardar Cambios del Curso"
// };

// // Eliminar Sección (Ya existía, funciona con las nuevas también)
// const handleDeleteSection = (sectionId:any) => {
//     setItemToDelete({ type: 'section', id: sectionId, cursoId: editingCursoId });
//     setIsDeleteDialogOpen(true);
// };

// // Cambios en Subsección
// const handleSubSectionChange = (sectionId:any, subSectionId:any, field:any, value:any) => {
//      setCurrentCursoData((prev:any) => {
//         if (!prev) return null;
//         const updatedSections = prev.sections.map((sec:any) => {
//             if (sec.id === sectionId) {
//                 const updatedSubSections = sec.subSections.map((sub:any) =>
//                     sub.id === subSectionId ? { ...sub, [field]: value } : sub
//                 );
//                 // Actualizar también el array de IDs por si acaso (aunque no debería cambiar aquí)
//                 const updatedSubSectionIds = updatedSubSections.map((sub:any) => sub.id);
//                 return { ...sec, subSections: updatedSubSections, subSectionsIds: updatedSubSectionIds };
//             }
//             return sec;
//         });
//         return { ...prev, sections: updatedSections };
//     });
// };

// // Añadir Subsección (Ya existía)
// const handleAddSubSection = (sectionId:any) => {
//     const newSubSectionId = `sub-new-${Date.now()}`;
//     const newSubSection = {
//         id: newSubSectionId,
//         body: "<p>Nuevo contenido...</p>",
//         sectionId: sectionId,
//         filesIds: [],
//         imagesIds: [],
//         isNew: true, // Marcar como nueva (opcional)
//     };
//     setCurrentCursoData((prev:any) => {
//         if (!prev) return null;
//         const updatedSections = prev.sections.map((sec:any) => {
//             if (sec.id === sectionId) {
//                 return {
//                     ...sec,
//                     subSectionsIds: [...sec.subSectionsIds, newSubSectionId],
//                     subSections: [...sec.subSections, newSubSection],
//                 };
//             }
//             return sec;
//         });
//         return { ...prev, sections: updatedSections };
//     });
// };

//  // Eliminar Subsección (Ya existía)
// const handleDeleteSubSection = (sectionId:any, subSectionId:any) => {
//     setItemToDelete({ type: 'subsection', id: subSectionId, sectionId: sectionId, cursoId: editingCursoId });
//     setIsDeleteDialogOpen(true);
// };

// // --- Guardado General ---
// const handleSaveCursoChanges = () => {
//     if (!currentCursoData) return;
//     setIsSaving(true); // Inicia estado de guardado
//     console.log("Guardando cambios para el Curso:", currentCursoData);

//     // --- Lógica API COMPLEJA (Simulación) ---
//     // 1. Preparar Payloads:
//     const cursoPayload = { // Datos del curso
//         id: currentCursoData.id,
//         title: currentCursoData.title,
//         description: currentCursoData.description,
//         sectionIds: currentCursoData.sections.map((s:any) => s.id), // Orden actualizado!
//         // ... otros campos del curso
//     };
//     const sectionsPayload = { // Secciones (nuevas y modificadas)
//         create: currentCursoData.sections.filter((s:any) => s.isNew).map((s:any) => ({ ...s, isNew: undefined })), // Quitar flag isNew
//         update: currentCursoData.sections.filter((s:any) => !s.isNew /* && s.hasChanged (necesitarías trackear cambios) */ ).map((s:any) => ({...s})),
//         // Necesitarías IDs de secciones eliminadas si no las quitas del estado global antes
//     };
//     const subSectionsPayload = { // Subsecciones (nuevas y modificadas)
//          create: currentCursoData.sections.flatMap((s:any) => s.subSections.filter((sub:any) => sub.isNew).map((sub:any) => ({...sub, isNew: undefined}))),
//          update: currentCursoData.sections.flatMap((s:any) => s.subSections.filter((sub:any) => !sub.isNew /* && sub.hasChanged */).map((sub:any) => ({...sub}))),
//          // IDs de subsecciones eliminadas...
//     };
//     // NOTA: Identificar secciones/subsecciones eliminadas es crucial. Podrías comparar
//     // `currentCursoData.sectionIds` con `cursos.find(c => c.id === editingCursoId).sectionIds` originales.

//     console.log("Payload Curso:", cursoPayload);
//     console.log("Payload Secciones:", sectionsPayload);
//     console.log("Payload Subsecciones:", subSectionsPayload);

//     // 2. Simular llamada(s) API
//     setTimeout(() => {
//         // 3. Actualizar Estado Global si API OK
//          setCursos((prevCursos:any) =>
//             prevCursos.map((c:any) =>
//                 c.id === cursoPayload.id ? { ...c, ...cursoPayload } : c
//             )
//         );
//          // Actualizar mapas globales (sections, subSections)
//          // Esto necesita reflejar lo que la API devolvió (IDs reales, etc.)
//          const updatedSectionsMap = { ...sections };
//          sectionsPayload.create.forEach((s:any) => updatedSectionsMap[s.id] = { ...s, subSections: undefined }); // Añadir nuevas
//          sectionsPayload.update.forEach((s:any) => updatedSectionsMap[s.id] = { ...sections[s.id], ...s, subSections: undefined }); // Actualizar existentes
//          // Faltaría eliminar secciones borradas del mapa global

//          const updatedSubSectionsMap = { ...subSections };
//          subSectionsPayload.create.forEach((sub:any) => updatedSubSectionsMap[sub.id] = sub);
//          subSectionsPayload.update.forEach((sub:any) => updatedSubSectionsMap[sub.id] = { ...subSections[sub.id], ...sub });
//          // Faltaría eliminar subsecciones borradas

//          setSections(updatedSectionsMap);
//          setSubSections(updatedSubSectionsMap);


//         setIsSaving(false); // Termina estado de guardado
//         setEditingCursoId(null);
//         setCurrentCursoData(null);
//         console.log("Cambios guardados (simulado).");
//         // Mostrar notificación Éxito
//     }, 1500); // Simular delay
// };

// // --- Confirmación de Eliminación (Adaptada) ---
//  const confirmDelete = () => {
//     if (!itemToDelete) return;
//     setIsLoading(true); // Usar isLoading general para borrado

//     const { type, id, sectionId, cursoId } = itemToDelete;

//     // Lógica de eliminación (Simulada - Debería llamar a API)
//     console.log(`Intentando eliminar ${type} con ID: ${id}`);

//     setTimeout(() => { // Simular llamada API
//         if (type === "news") {
//             //setNews((prev:any) => prev.filter((item:any) => item.id !== id));
//         } else if (type === "curso") {
//             setCursos((prev:any) => prev.filter((c:any) => c.id !== id));
//             // Limpiar secciones/subs huérfanas (idealmente backend lo hace)
//              const cursoEliminado = cursos.find((c:any) => c.id === id);
//              if (cursoEliminado) {
//                  const sectionsAEliminar = cursoEliminado.sectionIds;
//                  const subsAEliminar = Object.values(sections)
//                     .filter((s:any) => sectionsAEliminar.includes(s.id))
//                     .flatMap((s:any) => s.subSectionsIds);

//                  setSections((prev:any) => {
//                      const next = {...prev};
//                      sectionsAEliminar.forEach((sid:any) => delete next[sid]);
//                      return next;
//                  });
//                  setSubSections((prev:any) => {
//                      const next = {...prev};
//                      subsAEliminar.forEach(subid => delete next[subid]);
//                      return next;
//                  });
//              }
//             if (editingCursoId === id) handleCancelEditCurso();

//         } else if (type === "section") {
//              // Eliminar del ESTADO DE EDICIÓN ACTUAL
//              setCurrentCursoData((prev:any) => {
//                  if (!prev || prev.id !== cursoId) return prev;
//                  const updatedSections = prev.sections.filter((sec:any) => sec.id !== id);
//                  const updatedSectionIds = prev.sectionIds.filter((secId:any) => secId !== id);
//                  return { ...prev, sections: updatedSections, sectionIds: updatedSectionIds };
//              });
//              // NOTA: La eliminación permanente (API y estado global) idealmente
//              // se maneja en el GUARDADO. O podrías hacer la llamada API aquí.
//              // Si se hace aquí, también hay que eliminar del mapa global `sections`
//              // y sus subsecciones del mapa `subSections`.

//         } else if (type === "subsection") {
//              // Eliminar del ESTADO DE EDICIÓN ACTUAL
//              setCurrentCursoData((prev:any) => {
//                  if (!prev || prev.id !== cursoId) return prev;
//                  const updatedSections = prev.sections.map((sec:any) => {
//                      if (sec.id === sectionId) {
//                          const updatedSubSections = sec.subSections.filter((sub:any) => sub.id !== id);
//                          const updatedSubSectionIds = sec.subSectionsIds.filter((subId:any) => subId !== id);
//                          return { ...sec, subSections: updatedSubSections, subSectionsIds: updatedSubSectionIds };
//                      }
//                      return sec;
//                  });
//                  return { ...prev, sections: updatedSections };
//              });
//              // Igual que con section, la eliminación permanente idealmente es al guardar.
//              // Si se hace aquí, eliminar del mapa global `subSections`.
//         }

//         setIsLoading(false);
//         setIsDeleteDialogOpen(false);
//         setItemToDelete(null);
//         console.log(`${type} con ID: ${id} eliminado (simulado).`);
//          // Notificación de éxito (opcional)
//     }, 500); // Simular delay API
// };
// En un archivo separado, ej: /lib/apiClient.js o /services/api.js
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'https://sistema-gestion-1.onrender.com/api'; // Ajusta según tu config

async function apiClient(endpoint:string, method:string = 'GET', body:any = null, options:any = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const headers = {
        'Content-Type': 'application/json',
        // 'Authorization': `Bearer ${getToken()}`, // <-- Añade tu lógica de token aquí
        ...options.headers,
    };

    const config = {
        method,
        headers,
        ...options, // Permite pasar otras opciones de fetch
    };

    if (body) {
        // Si el body es FormData, no establezcas Content-Type manualmente
        if (body instanceof FormData) {
            delete headers['Content-Type'];
            config.body = body;
        } else {
            config.body = JSON.stringify(body);
        }
    }

    try {
        const response = await fetch(url, config);

        if (!response.ok) {
            let errorData;
            try {
                errorData = await response.json();
            } catch (e) {
                errorData = { message: response.statusText };
            }
            console.error(`API Error ${response.status} on ${method} ${endpoint}:`, errorData);
            throw new Error(errorData.message || `Request failed with status ${response.status}`);
        }

        // Si la respuesta no tiene contenido (ej: DELETE, PUT sin retorno)
        if (response.status === 204 || response.headers.get('content-length') === '0') {
            return null; // O un objeto indicando éxito, ej: { success: true }
        }

        return await response.json();
    } catch (error) {
        console.error(`Network or other error for ${method} ${endpoint}:`, error);
        throw error; // Re-lanzar para que el componente pueda manejarlo
    }
}

// --- Funciones específicas ---

// Cursos (Asumidos)
export const fetchCursosApi = () => apiClient('/courses/'); // Asume GET /api/courses
export const fetchCursoByIdApi = (id:any) => apiClient(`/courses/${id}`); // Asume GET /api/courses/{id}
export const updateCursoApi = (id:any, data:any) => apiClient(`/courses/${id}`, 'PUT', data); // Asume PUT /api/courses/{id}
// export const createCursoApi = (data) => apiClient('/courses', 'POST', data); // Asume POST /api/courses
export const deleteCursoApi = (id:any) => apiClient(`/courses/${id}`, 'DELETE'); // Asume DELETE /api/courses/{id}

// Secciones
export const createSectionApi = (data:any) => apiClient('/course-sections/createSection', 'POST', data); // POST /api/course-sections/createSection (body: { name, description, courseId })
export const fetchSectionByIdApi = (id:any) => apiClient(`/course-sections/getSectionById/${id}`); // GET /api/course-sections/getSectionById/{id}
export const updateSectionApi = (id:any, data:any) => apiClient(`/course-sections/${id}`, 'PUT', data); // PUT /api/course-sections/{id} (body: { name, description })
export const deleteSectionApi = (id:any) => apiClient(`/course-sections/${id}`, 'DELETE'); // DELETE /api/course-sections/{id}

// Subsecciones
export const createSubSectionApi = (sectionId:any, data:any) => apiClient(`/course-sections/${sectionId}/subseccion`, 'POST', data); // POST /api/course-sections/{id}/subseccion (body: { body })
export const fetchSubSectionByIdApi = (id:any) => apiClient(`/course-subsections/${id}`); // GET /api/course-subsections/{id}
export const updateSubSectionApi = (id:any, data:any) => apiClient(`/course-subsections/${id}`, 'PUT', data); // PUT /api/course-subsections/{id} (body: { body, filesIds?, imagesIds? }) <- IMPORTANTE incluir file/image IDs
export const deleteSubSectionApi = (id:any) => apiClient(`/course-subsections/${id}`, 'DELETE'); // DELETE /api/course-subsections/{id}
export const addFileToSubSectionApi = (subSectionId:any, file:any) => apiClient(`/course-subsections/${subSectionId}/addFile`, 'POST', { fileName:file.name, newFile:file.file }); // POST /api/course-subsections/{subSectionId}/addFile (body: { fileId })

// Archivos (y Imágenes tratadas como archivos)
export const uploadFileApi = (formData:any) => apiClient('/files/subir', 'POST', formData); // POST /api/files/subir (body: FormData)
export const fetchFilesApi = () => apiClient('/files'); // GET /api/files
export const fetchFileByIdApi = (id:any) => apiClient(`/files/${id}`); // GET /api/files/{id}
export const deleteFileApi = (id:any) => apiClient(`/files/${id}`, 'DELETE'); // DELETE /api/files/{id}

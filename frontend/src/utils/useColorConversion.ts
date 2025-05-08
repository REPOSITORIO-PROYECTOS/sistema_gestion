// utils/useColorConversion.ts
export const oklchToRgb = (oklch: string): string => {
    // Aquí puedes agregar la lógica para convertir oklch a rgb.
    // Esto es solo un ejemplo simplificado. En producción, puedes usar librerías
    // como `oklch-to-rgb` para hacer esta conversión correctamente.

    const match = oklch.match(/^oklch\(([^,]+),([^,]+),([^,]+)\)$/);
    if (match) {
        const l = parseFloat(match[1]);
        const c = parseFloat(match[2]);
        const h = parseFloat(match[3]);

        // Implementar fórmula de conversión a RGB según el espacio de color OKLCH
        // Puedes usar alguna librería como "oklch-to-rgb" para hacerlo correctamente.
        const rgb = `rgb(${Math.round(l * 255)}, ${Math.round(c * 255)}, ${Math.round(h * 255)})`;
        return rgb;
    }

    return 'rgb(0, 0, 0)'; // Default fallback color
};

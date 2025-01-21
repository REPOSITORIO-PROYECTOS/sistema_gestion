import Link from 'next/link'
import { Button } from "@/components/ui/button"

export function Navbar() {
    return (
        <nav className="bg-white shadow-md">
            <div className="container mx-auto px-6 py-3 flex justify-start items-center space-x-4">
                <Button className='bg-blue-700 hover:bg-blue-900' asChild>
                    <Link href="/">Home</Link>
                </Button>
                <Button variant="ghost" className='hover:bg-blue-700 hover:text-white' asChild>
                    <Link href="#">Inventario</Link>
                </Button>
                <Button variant="ghost" className='hover:bg-blue-700 hover:text-white' asChild>
                    <Link href="/cursos">Cursos</Link>
                </Button>
                <Button variant="ghost" className='hover:bg-blue-700 hover:text-white' asChild>
                    <Link href="#">Usuarios</Link>
                </Button>
                <Button variant="ghost" className='hover:bg-blue-700 hover:text-white' asChild>
                    <Link href="#">Historial</Link>
                </Button>
            </div>
        </nav>
    )
}


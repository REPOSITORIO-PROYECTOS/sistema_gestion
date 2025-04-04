

export default function Estadisticas() {
    return (
        <section className="container flex justify-around items-center gap-8 mx-auto">
            <div className="w-full flex justify-between items-center h-24 px-6 rounded-md bg-[#0b90d1]" >
                <div className="text-white text-lg font-bold leading-relaxed">Deuda 5 dias </div>
                <div className="text-white text-4xl font-bold leading-relaxed">2</div>
            </div>
            <div className="w-full flex justify-between items-center h-24 px-6 rounded-md bg-[#0b90d1]" >
                <div className="text-white text-lg font-bold leading-relaxed">Deuda 15 dias </div>
                <div className="text-white text-4xl font-bold leading-relaxed">1</div>
            </div>
            <div className="w-full flex justify-between items-center h-24 px-6 rounded-md bg-[#0b90d1]" >
                <div className="text-white text-lg font-bold leading-relaxed">Deuda 30 dias </div>
                <div className="text-white text-4xl font-bold leading-relaxed">4</div>
            </div>
            <div className="w-full flex justify-between items-center h-24 px-6 rounded-md bg-[#0b90d1]" >
                <div className="text-white text-lg font-bold leading-relaxed">Total coutas deudas </div>
                <div className="text-white text-4xl font-bold leading-relaxed">7</div>
            </div>
        </section>
    )
}

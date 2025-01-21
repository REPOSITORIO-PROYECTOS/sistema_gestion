import AgregarCurso from '@/components/agregar-curso'
import Panel from '@/components/interfaces/panel'
import TableFilter from '@/components/interfaces/table-filter'
import React from 'react'

export default function Page() {
    return (
        <>
            <Panel />
            <AgregarCurso />
            <TableFilter />
        </>
    )
}

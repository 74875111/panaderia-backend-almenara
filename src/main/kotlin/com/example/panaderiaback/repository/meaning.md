# Que va aqui
Para qué sirve: Contiene interfaces que acceden a la base de datos, generalmente extendiendo JpaRepository o CrudRepository.

Ejemplo:

ProductoRepository.java: te permite hacer cosas como findByNombre("Baguette") o findAll() sin escribir SQL directamente.
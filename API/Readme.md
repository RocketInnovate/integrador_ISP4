# PassKeeperAPI

Este proyecto es una API REST construida con Django REST framework que permite a los usuarios de la aplicación Android PassKeeper realizar la compra de la version premium de la misma.

## Empezando

Estas instrucciones te permitirán obtener una copia del proyecto y ejecutarla en tu máquina local para propósitos de desarrollo y prueba.

### Pre-requisitos

Necesitarás Python y `virtualenv` instalado en tu sistema. Si no tienes `virtualenv`, puedes instalarlo con pip:

```bash
pip install virtualenv
```

### Configuración del entorno de desarrollo

1- Primero, clona el repositorio en tu máquina local y navega al directorio clonado:
```bash
git clone https://github.com/RocketInnovate/integrador_ISP4.git
```
```bash
cd integrador_ISP4/API
```


2- Crea un entorno virtual y actívalo:
```bash
virtualenv venv
```
Windows:
```bash
    venv\Scripts\activate  
```
linux:
```bash
    source venv/bin/activate
```

3 -Instala las dependencias del proyecto:
```bash
pip install -r requirements.txt
```
4- Realiza las migraciones necesarias para preparar la base de datos:
```bash
python manage.py migrate
```
5- Crea un usuario administrador para acceder a la interfaz de administración de Django:
```bash
python manage.py createsuperuser
```

### Ejecución
Inicia el servidor de desarrollo:
```bash
python manage.py runserver
```

Puedes acceder a la API en http://localhost:8000/.


// Dirección de la API. Como el HTML lo sirve el propio Spring Boot,
// vale con la ruta relativa (http://localhost:8080 + la ruta).
const API = "http://localhost:8080";

// ===================== MENSAJES =====================

function mostrarMensaje(texto, esError) {
    const caja = document.getElementById("mensaje");
    caja.textContent = texto;
    caja.className = esError ? "error" : "ok";
    caja.style.display = "block";
    setTimeout(() => caja.style.display = "none", 3000);
}

// Traduce el código de respuesta de la API a un mensaje entendible
function mensajeError(status) {
    if (status === 400) return "Datos incorrectos (400 Bad Request)";
    if (status === 404) return "No encontrado (404 Not Found)";
    return "Error " + status;
}

// ===================== VIDEOJUEGOS =====================

async function cargarVideojuegos() {
    const respuesta = await fetch(API + "/videojuegos");
    const videojuegos = await respuesta.json();

    // Tabla
    let filas = "";
    for (const v of videojuegos) {
        filas += `<tr><td>${v.id}</td><td>${v.nombre}</td><td>${v.genero}</td></tr>`;
    }
    document.getElementById("tablaVideojuegos").innerHTML = filas;

    // Desplegables que necesitan los videojuegos
    let opciones = "";
    for (const v of videojuegos) {
        opciones += `<option value="${v.id}">${v.nombre}</option>`;
    }
    document.getElementById("torneoVideojuego").innerHTML = opciones;
    document.getElementById("filtroVideojuego").innerHTML =
        `<option value="">Todos los videojuegos</option>` + opciones;
}

document.getElementById("formVideojuego").addEventListener("submit", async (e) => {
    e.preventDefault();
    const videojuego = {
        nombre: document.getElementById("videojuegoNombre").value,
        genero: document.getElementById("videojuegoGenero").value
    };

    const respuesta = await fetch(API + "/videojuegos", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(videojuego)
    });

    if (respuesta.status === 201) {
        mostrarMensaje("Videojuego creado", false);
        e.target.reset();
        cargarVideojuegos();
    } else {
        mostrarMensaje(mensajeError(respuesta.status), true);
    }
});

// ===================== JUGADORES =====================

async function cargarJugadores() {
    const respuesta = await fetch(API + "/jugadores");
    const jugadores = await respuesta.json();

    let filas = "";
    for (const j of jugadores) {
        filas += `<tr>
            <td>${j.id}</td>
            <td>${j.nick}</td>
            <td>${j.email}</td>
            <td>${j.nivel}</td>
            <td>
                <button onclick="editarJugador(${j.id})">Editar</button>
                <button class="secundario" onclick="verParticipacionesJugador(${j.id})">Torneos</button>
                <button class="borrar" onclick="borrarJugador(${j.id})">Borrar</button>
            </td>
        </tr>`;
    }
    document.getElementById("tablaJugadores").innerHTML = filas;

    // Desplegable del formulario de inscripción
    let opciones = "";
    for (const j of jugadores) {
        opciones += `<option value="${j.id}">${j.nick}</option>`;
    }
    document.getElementById("participacionJugador").innerHTML = opciones;
}

// Crear (POST) o modificar (PUT), según si hay un id guardado en el campo oculto
document.getElementById("formJugador").addEventListener("submit", async (e) => {
    e.preventDefault();
    const id = document.getElementById("jugadorId").value;
    const jugador = {
        nick: document.getElementById("jugadorNick").value,
        email: document.getElementById("jugadorEmail").value,
        nivel: parseInt(document.getElementById("jugadorNivel").value)
    };

    let respuesta;
    if (id === "") {
        respuesta = await fetch(API + "/jugadores", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(jugador)
        });
    } else {
        respuesta = await fetch(API + "/jugadores/" + id, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(jugador)
        });
    }

    if (respuesta.ok) {
        mostrarMensaje(id === "" ? "Jugador creado" : "Jugador modificado", false);
        limpiarFormJugador();
        cargarJugadores();
        cargarParticipaciones();
    } else {
        mostrarMensaje(mensajeError(respuesta.status), true);
    }
});

// Busca el jugador por id (GET /jugadores/{id}) y rellena el formulario
async function editarJugador(id) {
    const respuesta = await fetch(API + "/jugadores/" + id);
    if (!respuesta.ok) {
        mostrarMensaje(mensajeError(respuesta.status), true);
        return;
    }
    const j = await respuesta.json();
    document.getElementById("jugadorId").value = j.id;
    document.getElementById("jugadorNick").value = j.nick;
    document.getElementById("jugadorEmail").value = j.email;
    document.getElementById("jugadorNivel").value = j.nivel;
    document.getElementById("botonJugador").textContent = "Guardar cambios";
}

function limpiarFormJugador() {
    document.getElementById("formJugador").reset();
    document.getElementById("jugadorId").value = "";
    document.getElementById("botonJugador").textContent = "Crear jugador";
}

document.getElementById("cancelarJugador").addEventListener("click", limpiarFormJugador);

async function borrarJugador(id) {
    if (!confirm("¿Seguro que quieres borrar este jugador y sus participaciones?")) return;

    const respuesta = await fetch(API + "/jugadores/" + id, { method: "DELETE" });
    if (respuesta.status === 204) {
        mostrarMensaje("Jugador eliminado", false);
        document.getElementById("participacionesJugador").innerHTML = "";
        cargarJugadores();
        cargarParticipaciones();
    } else {
        mostrarMensaje(mensajeError(respuesta.status), true);
    }
}

// GET /jugadores/{id}/participaciones
async function verParticipacionesJugador(id) {
    const respuesta = await fetch(API + "/jugadores/" + id + "/participaciones");
    if (!respuesta.ok) {
        mostrarMensaje(mensajeError(respuesta.status), true);
        return;
    }
    const participaciones = await respuesta.json();
    const caja = document.getElementById("participacionesJugador");

    if (participaciones.length === 0) {
        caja.innerHTML = "<p>Este jugador no ha participado en ningún torneo.</p>";
        return;
    }

    let html = `<h3>Torneos de ${participaciones[0].jugador.nick}</h3>
        <table><tr><th>Torneo</th><th>Videojuego</th><th>Puntos</th><th>Posición</th></tr>`;
    for (const p of participaciones) {
        html += `<tr>
            <td>${p.torneo.nombre}</td>
            <td>${p.torneo.videojuego.nombre}</td>
            <td>${p.puntos}</td>
            <td>${p.posicion}</td>
        </tr>`;
    }
    html += "</table>";
    caja.innerHTML = html;
}

// ===================== TORNEOS =====================

// Pinta una lista de torneos en la tabla
function pintarTorneos(torneos) {
    let filas = "";
    for (const t of torneos) {
        filas += `<tr>
            <td>${t.id}</td>
            <td>${t.nombre}</td>
            <td>${t.fecha}</td>
            <td>${t.premio} €</td>
            <td>${t.estado}</td>
            <td>${t.videojuego.nombre}</td>
            <td>
                <button onclick="editarTorneo(${t.id})">Editar</button>
                <button class="secundario" onclick="verParticipantes(${t.id})">Participantes</button>
                <button class="borrar" onclick="borrarTorneo(${t.id})">Borrar</button>
            </td>
        </tr>`;
    }
    document.getElementById("tablaTorneos").innerHTML = filas;
}

async function cargarTorneos() {
    const respuesta = await fetch(API + "/torneos");
    const torneos = await respuesta.json();
    pintarTorneos(torneos);

    // Desplegable del formulario de inscripción
    let opciones = "";
    for (const t of torneos) {
        opciones += `<option value="${t.id}">${t.nombre}</option>`;
    }
    document.getElementById("participacionTorneo").innerHTML = opciones;
}

// Buscar: elige la ruta según los filtros que se hayan marcado
document.getElementById("botonFiltrar").addEventListener("click", async () => {
    const videojuegoId = document.getElementById("filtroVideojuego").value;
    const estado = document.getElementById("filtroEstado").value;

    let ruta;
    if (videojuegoId !== "" && estado !== "") {
        ruta = "/torneos/videojuego/" + videojuegoId + "/estado/" + estado;
    } else if (videojuegoId !== "") {
        ruta = "/torneos/videojuego/" + videojuegoId;
    } else if (estado !== "") {
        ruta = "/torneos/estado/" + estado;
    } else {
        ruta = "/torneos";
    }

    const respuesta = await fetch(API + ruta);
    if (!respuesta.ok) {
        mostrarMensaje(mensajeError(respuesta.status), true);
        return;
    }
    const torneos = await respuesta.json();
    pintarTorneos(torneos);
    mostrarMensaje(torneos.length + " torneo(s) encontrado(s)", false);
});

// Crear: POST /torneos?videojuegoId=X   Modificar: PUT /torneos/{id}
document.getElementById("formTorneo").addEventListener("submit", async (e) => {
    e.preventDefault();
    const id = document.getElementById("torneoId").value;
    const torneo = {
        nombre: document.getElementById("torneoNombre").value,
        fecha: document.getElementById("torneoFecha").value,
        premio: parseFloat(document.getElementById("torneoPremio").value),
        estado: document.getElementById("torneoEstado").value
    };
    const videojuegoId = document.getElementById("torneoVideojuego").value;

    let respuesta;
    if (id === "") {
        respuesta = await fetch(API + "/torneos?videojuegoId=" + videojuegoId, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(torneo)
        });
    } else {
        respuesta = await fetch(API + "/torneos/" + id, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(torneo)
        });
    }

    if (respuesta.ok) {
        mostrarMensaje(id === "" ? "Torneo creado" : "Torneo modificado", false);
        limpiarFormTorneo();
        cargarTorneos();
        cargarParticipaciones();
    } else {
        mostrarMensaje(mensajeError(respuesta.status), true);
    }
});

async function editarTorneo(id) {
    const respuesta = await fetch(API + "/torneos/" + id);
    if (!respuesta.ok) {
        mostrarMensaje(mensajeError(respuesta.status), true);
        return;
    }
    const t = await respuesta.json();
    document.getElementById("torneoId").value = t.id;
    document.getElementById("torneoNombre").value = t.nombre;
    document.getElementById("torneoFecha").value = t.fecha;
    document.getElementById("torneoPremio").value = t.premio;
    document.getElementById("torneoEstado").value = t.estado;
    document.getElementById("torneoVideojuego").value = t.videojuego.id;
    // Al modificar, el videojuego del torneo no se cambia
    document.getElementById("torneoVideojuego").disabled = true;
    document.getElementById("botonTorneo").textContent = "Guardar cambios";
}

function limpiarFormTorneo() {
    document.getElementById("formTorneo").reset();
    document.getElementById("torneoId").value = "";
    document.getElementById("torneoVideojuego").disabled = false;
    document.getElementById("botonTorneo").textContent = "Crear torneo";
}

document.getElementById("cancelarTorneo").addEventListener("click", limpiarFormTorneo);

async function borrarTorneo(id) {
    if (!confirm("¿Seguro que quieres borrar este torneo y sus participaciones?")) return;

    const respuesta = await fetch(API + "/torneos/" + id, { method: "DELETE" });
    if (respuesta.status === 204) {
        mostrarMensaje("Torneo eliminado", false);
        document.getElementById("participantesTorneo").innerHTML = "";
        cargarTorneos();
        cargarParticipaciones();
    } else {
        mostrarMensaje(mensajeError(respuesta.status), true);
    }
}

// GET /torneos/{id}/participantes
async function verParticipantes(id) {
    const respuesta = await fetch(API + "/torneos/" + id + "/participantes");
    if (!respuesta.ok) {
        mostrarMensaje(mensajeError(respuesta.status), true);
        return;
    }
    const participaciones = await respuesta.json();
    const caja = document.getElementById("participantesTorneo");

    if (participaciones.length === 0) {
        caja.innerHTML = "<p>Este torneo todavía no tiene participantes.</p>";
        return;
    }

    let html = `<h3>Participantes de ${participaciones[0].torneo.nombre}</h3>
        <table><tr><th>Jugador</th><th>Nivel</th><th>Puntos</th><th>Posición</th></tr>`;
    for (const p of participaciones) {
        html += `<tr>
            <td>${p.jugador.nick}</td>
            <td>${p.jugador.nivel}</td>
            <td>${p.puntos}</td>
            <td>${p.posicion}</td>
        </tr>`;
    }
    html += "</table>";
    caja.innerHTML = html;
}

// ===================== PARTICIPACIONES =====================

async function cargarParticipaciones() {
    const respuesta = await fetch(API + "/participaciones");
    const participaciones = await respuesta.json();

    let filas = "";
    for (const p of participaciones) {
        filas += `<tr>
            <td>${p.id}</td>
            <td>${p.jugador.nick}</td>
            <td>${p.torneo.nombre}</td>
            <td>${p.puntos}</td>
            <td>${p.posicion}</td>
        </tr>`;
    }
    document.getElementById("tablaParticipaciones").innerHTML = filas;
}

// POST /participaciones?jugadorId=X&torneoId=Y
document.getElementById("formParticipacion").addEventListener("submit", async (e) => {
    e.preventDefault();
    const jugadorId = document.getElementById("participacionJugador").value;
    const torneoId = document.getElementById("participacionTorneo").value;
    const participacion = {
        puntos: parseInt(document.getElementById("participacionPuntos").value),
        posicion: parseInt(document.getElementById("participacionPosicion").value)
    };

    const respuesta = await fetch(
        API + "/participaciones?jugadorId=" + jugadorId + "&torneoId=" + torneoId, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(participacion)
        });

    if (respuesta.status === 201) {
        mostrarMensaje("Jugador inscrito en el torneo", false);
        e.target.reset();
        cargarParticipaciones();
    } else {
        mostrarMensaje(mensajeError(respuesta.status), true);
    }
});

// ===================== AL ABRIR LA PÁGINA =====================

cargarVideojuegos();
cargarJugadores();
cargarTorneos();
cargarParticipaciones();
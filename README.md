# JavaKiller

Equipo de robots para **Robocode**. Los robots comparten una base y se reparten en dos grupos: JavaKillers y PredatorTeam.

## Qué hay

| Clase | Papel |
| --- | --- |
| `javaKillersBase` | Base abstracta que extiende `TeamRobot` |
| `javaKillersDroids` | Droides del equipo |
| `javaKillersLeader` | Líder del equipo |
| `PredatorTeam` | Robot del equipo Predator |
| `Enemy` | Datos de un enemigo |
| `Vector2` | Vector 2D de apoyo |

## Stack

- Java
- [Robocode](https://robocode.sourceforge.io/) (`robocode.TeamRobot`)
- Proyecto NetBeans (`build.xml`, `nbproject`)

## Cómo probarlo

1. Instala Robocode.
2. Compila el proyecto en NetBeans, o compila `src` con `robocode.jar` en el classpath.
3. Copia las clases generadas a la carpeta de robots de Robocode.
4. Arranca una batalla e incluye los robots del paquete `JavaKillers`.

`javaKillersBase` no se instancia sola: es la clase común del equipo.

# JavaKiller

A team of robots for **Robocode**. The robots share a base class and are split into two groups: JavaKillers and PredatorTeam.

## What's here

| Class | Role |
| --- | --- |
| `javaKillersBase` | Abstract base that extends `TeamRobot` |
| `javaKillersDroids` | Team droids |
| `javaKillersLeader` | Team leader |
| `PredatorTeam` | Predator team robot |
| `Enemy` | Enemy data |
| `Vector2` | Helper 2D vector |

## Stack

- Java
- [Robocode](https://robocode.sourceforge.io/) (`robocode.TeamRobot`)
- NetBeans project (`build.xml`, `nbproject`)

## How to try it

1. Install Robocode.
2. Build the project in NetBeans, or compile `src` with `robocode.jar` on the classpath.
3. Copy the generated classes into Robocode's robots folder.
4. Start a battle and add the robots from the `JavaKillers` package.

`javaKillersBase` is not meant to be instantiated on its own: it is the shared team class.

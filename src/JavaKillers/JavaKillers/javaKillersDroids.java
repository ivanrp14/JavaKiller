package JavaKillers.JavaKillers;

import JavaKillers.javaKillersBase;
import robocode.*;
import java.awt.geom.Point2D;
import java.io.IOException;

public class javaKillersDroids extends javaKillersBase {
    double leaderLife = 100;
    ScannedRobotEvent currentTarget = null;
    String role = ""; // Podría ser "Ramming", "Random", "Mirror", "Shield"
    Point2D.Double myPosition, leaderPosition, enemyPosition;
    double myEnergy;
    boolean isRamming = false;
    
    
    public void run() {
        // Asignar un rol aquí si es necesario, por ejemplo, role = "Ramming";
        
        while (true) {
            myPosition = new Point2D.Double(getX(), getY());
            myEnergy = getEnergy();
            
            // Comunicar información al líder
            try {
                broadcastMessage(myPosition);
                if (myEnergy < 20) {
                    broadcastMessage("LowEnergy");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            // Lógica de movimiento y disparo aquí
            switch (role) {
                case "Ramming":
                    if (enemyPosition != null) {
                        isRamming = true;
                        double angle = Math.atan2(enemyPosition.x - getX(), enemyPosition.y - getY());
                        angle = Math.toDegrees(angle);
                        turnRight(normalizeBearing(angle - getHeading()));
                        ahead(Double.MAX_VALUE); // Moverse hacia adelante indefinidamente hasta que algo suceda
                    }
                    break;
                case "Random":
                    double move = Math.random() * 100;
                    double turn = Math.random() * 360 - 180;
                    ahead(move);
                    turnGunRight(turn);
                    break;
                case "Mirror":
                    if (enemyPosition != null) {
                        // Moverse en la dirección opuesta al enemigo
                        double angle = Math.atan2(getX() - enemyPosition.x, getY() - enemyPosition.y);
                        angle = Math.toDegrees(angle);

                        // Girar y moverse
                        turnRight(normalizeBearing(angle - getHeading()));
                        ahead(100);
                    }
                    break;
                case "Shield":
                    if (leaderPosition != null && enemyPosition != null) {
                        // Calcular la dirección hacia el líder y hacia el enemigo
                        double angleToLeader = Math.atan2(leaderPosition.x - getX(), leaderPosition.y - getY());
                        double angleToEnemy = Math.atan2(enemyPosition.x - getX(), enemyPosition.y - getY());

                        // Normalizar y convertir a grados
                        angleToLeader = Math.toDegrees(angleToLeader);
                        angleToEnemy = Math.toDegrees(angleToEnemy);

                        // Intentar interponerse entre el líder y el enemigo
                        double interceptAngle = (angleToLeader + angleToEnemy) / 2;

                        // Girar hacia el ángulo de intercepción y moverse hacia él
                        turnRight(normalizeBearing(interceptAngle - getHeading()));
                        ahead(100);  // Mover una distancia para ajustar la posición

                        // Opcionalmente, disparar al enemigo para distraerlo
                        turnGunRight(normalizeBearing(angleToEnemy - getGunHeading()));
                        fire(2);
                    }
                    break;
                default:
                    // Código predeterminado
                    break;
            }
            
            execute();
        }
    }
    
    public double normalizeBearing(double angle){
        while(angle > 180) angle -= 360;
        while(angle < -180) angle += 360;
        return angle;
    }
    
    public void onHitRobot(HitRobotEvent e) {
        if (isRamming) {
            // Si estamos en modo Ramming, podríamos retroceder un poco y luego volver a cargar
            back(20);
            ahead(Double.MAX_VALUE);
        }
    }

    public void onMessageReceived(MessageEvent e) {
        Object msg = e.getMessage();
        String sender = e.getSender();
        
        if (msg instanceof Double && "Leader".equals(e.getSender())) {
            leaderLife = (Double) msg;
        }
        if (msg instanceof Point2D.Double && "Leader".equals(sender)) {
            leaderPosition = (Point2D.Double) msg;
        }
        if ("Leader".equals(e.getSender())) {
            if (e.getMessage() instanceof Point2D.Double) {
                Point2D.Double enemyPos = (Point2D.Double) e.getMessage();
                // Ahora este droide tiene la posición del enemigo más cercano y puede tomar decisiones basadas en ella.
                this.enemyPosition = enemyPos;
            }
        }
    }

    }
    
    /*public void onScannedRobot(ScannedRobotEvent e) {
        if (isTeammate(e.getName())) {
            return;
        }

        // Selección de objetivos
        if (currentTarget == null || e.getDistance() < currentTarget.getDistance()) {
            currentTarget = e;
        }

        if (leaderLife < 50) {
            // Proteger al líder si el rol es "Shield"
            if ("Shield".equals(role)) {
                turnRight(e.getBearing());
                ahead(100);
            }
        } else {
            // Implementar lógica de movimiento y disparo basada en el rol
        }

        // Disparo adaptativo
        double firePower = e.getDistance() > 200 ? 1 : 3;
        for (int i = -2; i <= 2; i++) {
            double adjustedAngle = e.getBearing() + i * 5;
            turnGunRight(adjustedAngle);
            fire(firePower);
        }
        
        // Informar al líder si un enemigo ha sido derribado
        if (e.getEnergy() == 0) {
            try {
                broadcastMessage("EnemyDown");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }*/

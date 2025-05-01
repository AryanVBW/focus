import { jsx as _jsx } from "react/jsx-runtime";
import { useCallback } from 'react';
import Particles from "react-tsparticles";
import { loadSlim } from "tsparticles-slim";
import { useTheme } from '../context/ThemeContext';

const AnimatedBackground = () => {
    const { theme } = useTheme();
    
    // Particle configuration based on theme
    const getParticleConfig = () => {
        return {
            particles: {
                number: {
                    value: 80,
                    density: {
                        enable: true,
                        value_area: 800
                    }
                },
                color: {
                    value: theme === 'dark' ? "#a78bfa" : "#6d28d9",
                },
                opacity: {
                    value: 0.2,
                    random: true,
                },
                size: {
                    value: 3,
                    random: true,
                },
                move: {
                    enable: true,
                    speed: 0.5,
                    direction: "none",
                    random: true,
                    straight: false,
                    out_mode: "out",
                    bounce: false,
                },
                line_linked: {
                    enable: true,
                    distance: 150,
                    color: theme === 'dark' ? "#8b5cf6" : "#6d28d9",
                    opacity: 0.1,
                    width: 1
                },
            },
            interactivity: {
                detect_on: "canvas",
                events: {
                    onhover: {
                        enable: true,
                        mode: "grab"
                    },
                    onclick: {
                        enable: true,
                        mode: "push"
                    },
                    resize: true
                },
                modes: {
                    grab: {
                        distance: 140,
                        line_linked: {
                            opacity: 0.4
                        }
                    },
                    push: {
                        particles_nb: 3
                    }
                }
            },
            retina_detect: true,
            background: {
                color: "transparent",
                position: "50% 50%",
                repeat: "no-repeat",
                size: "cover"
            }
        };
    };

    const particlesInit = useCallback(async (engine) => {
        await loadSlim(engine);
    }, []);

    return (
        <div className="fixed inset-0 -z-10">
            <Particles
                id="tsparticles"
                init={particlesInit}
                options={getParticleConfig()}
                className="absolute inset-0"
            />
        </div>
    );
};

export default AnimatedBackground;

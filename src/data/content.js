export const navLinks = [
    { name: 'Home', href: 'home' },
    { name: 'About', href: 'about' },
    { name: 'Problem', href: 'problem' },
    { name: 'Solution', href: 'solution' },
    { name: 'Features', href: 'features' },
    { name: 'App Overview', href: 'app-overview', isExternalPage: true },
    { name: 'Documentation', href: 'documentation', isExternalPage: true },
    { name: 'Download', href: 'download' },
];
export const problems = [
    {
        id: 'productivity',
        title: 'Reduced Productivity',
        description: 'Constant distractions from apps and notifications decrease focus and work efficiency.',
        icon: 'clock',
    },
    {
        id: 'anxiety',
        title: 'Anxiety and FOMO',
        description: 'Fear of missing out and social media addiction leads to stress and anxiety.',
        icon: 'brain',
    },
    {
        id: 'sleep',
        title: 'Sleep Disruption',
        description: 'Blue light and late-night scrolling negatively impacts sleep quality and health.',
        icon: 'moon',
    },
    {
        id: 'social',
        title: 'Less Real-World Interaction',
        description: 'Excessive screen time reduces meaningful face-to-face social interactions.',
        icon: 'users',
    },
];
export const solutions = [
    {
        id: 'normal-mode',
        title: 'Normal Mode',
        description: 'Monitor your digital habits, set limits, and get insights on your app usage patterns to build awareness.',
        icon: 'activity',
        mode: 'normal',
    },
    {
        id: 'focus-mode',
        title: 'Focus Mode',
        description: 'Block distracting apps and content during crucial work or study hours to maintain complete concentration.',
        icon: 'zap',
        mode: 'focus',
    },
];
export const features = [
    {
        id: 'content-blocking',
        title: 'Smart Content Blocking',
        description: 'Intelligently filters out distracting content across websites and apps to keep you focused.',
        icon: 'shield',
        color: 'bg-red-100 text-red-600',
    },
    {
        id: 'adult-filter',
        title: 'Adult Content Filter',
        description: 'Powerful filtering technology blocks inappropriate content to create a healthier digital environment.',
        icon: 'eye-off',
        color: 'bg-orange-100 text-orange-600',
    },
    {
        id: 'app-management',
        title: 'App Management',
        description: 'Control which apps you can use during focus sessions, with customizable schedules and exceptions.',
        icon: 'smartphone',
        color: 'bg-blue-100 text-blue-600',
    },
    {
        id: 'notification-control',
        title: 'Notification Control',
        description: 'Silence non-essential notifications during focus time while allowing important alerts through.',
        icon: 'bell-off',
        color: 'bg-purple-100 text-purple-600',
    },
];
export const howItWorksSteps = [
    {
        id: 'step1',
        title: 'Detect Content Types',
        description: 'Our advanced AI analyzes content in real-time to identify distracting or unwanted material.',
        icon: 'search',
    },
    {
        id: 'step2',
        title: 'Recognize Apps',
        description: 'Focus identifies apps by category and helps you manage which ones can run during focus time.',
        icon: 'layers',
    },
    {
        id: 'step3',
        title: 'Block and Redirect',
        description: 'When distracting content is detected, Focus blocks it and redirects you to productive alternatives.',
        icon: 'shield',
    },
    {
        id: 'step4',
        title: 'Show Blocking Pages',
        description: 'Friendly reminders appear when you attempt to access blocked content during focus sessions.',
        icon: 'alert-circle',
    },
];
export const permissions = [
    {
        id: 'perm1',
        title: 'Accessibility Service',
        description: 'Allows Focus to identify running apps and content to enable blocking functionality.',
        icon: 'eye',
    },
    {
        id: 'perm2',
        title: 'Usage Stats',
        description: 'Helps track app usage time to provide insights and enforce time limits.',
        icon: 'bar-chart-2',
    },
    {
        id: 'perm3',
        title: 'Notification Access',
        description: 'Required to filter and manage notifications during focus sessions.',
        icon: 'bell',
    },
    {
        id: 'perm4',
        title: 'Draw Over Other Apps',
        description: 'Enables Focus to display blocking screens when you try to access restricted content.',
        icon: 'layers',
    },
];

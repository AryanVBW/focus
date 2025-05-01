export interface NavLink {
  name: string;
  href: string;
}

export interface Feature {
  id: string;
  title: string;
  description: string;
  icon: string;
  color?: string;
}

export interface Problem {
  id: string;
  title: string;
  description: string;
  icon: string;
}

export interface Solution {
  id: string;
  title: string;
  description: string;
  icon: string;
  mode: 'normal' | 'focus';
}

export interface Step {
  id: string;
  title: string;
  description: string;
  icon: string;
}

export interface Permission {
  id: string;
  title: string;
  description: string;
  icon: string;
}
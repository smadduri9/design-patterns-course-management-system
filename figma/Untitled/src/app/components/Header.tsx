import { BookOpen, Bell, User, GraduationCap, UserCog } from 'lucide-react';
import { Link, useLocation } from 'react-router';

interface HeaderProps {
  role?: 'instructor' | 'student';
}

export function Header({ role = 'instructor' }: HeaderProps) {
  const location = useLocation();

  return (
    <header className="fixed top-0 left-0 right-0 h-16 bg-card border-b border-border z-10">
      <div className="h-full px-6 flex items-center justify-between">
        <Link to="/" className="flex items-center gap-3 hover:opacity-80 transition-opacity">
          <BookOpen className="w-6 h-6 text-primary" />
          <div>
            <h1 className="text-lg font-semibold tracking-tight">design-patterns-course-management-system</h1>
            <p className="text-xs text-muted-foreground -mt-0.5">Design Patterns course project</p>
          </div>
        </Link>

        <div className="flex items-center gap-2">
          <Link
            to="/"
            className={`flex items-center gap-2 px-3 py-2 rounded-lg transition-colors text-sm ${
              location.pathname === '/' || location.pathname.includes('course') || location.pathname.includes('submission')
                ? 'bg-primary/10 text-primary'
                : 'hover:bg-muted'
            }`}
          >
            <UserCog className="w-4 h-4" />
            Instructor View
          </Link>
          <Link
            to="/student"
            className={`flex items-center gap-2 px-3 py-2 rounded-lg transition-colors text-sm ${
              location.pathname === '/student'
                ? 'bg-primary/10 text-primary'
                : 'hover:bg-muted'
            }`}
          >
            <GraduationCap className="w-4 h-4" />
            Student View
          </Link>
        </div>

        <div className="flex items-center gap-4">
          <div className="text-sm text-muted-foreground">
            Role: <span className="font-medium text-foreground capitalize">{role}</span>
          </div>
          <button className="relative p-2 hover:bg-muted rounded-lg transition-colors">
            <Bell className="w-5 h-5" />
            <span className="absolute top-1 right-1 w-2 h-2 bg-accent rounded-full"></span>
          </button>
          <button className="p-2 hover:bg-muted rounded-lg transition-colors">
            <User className="w-5 h-5" />
          </button>
        </div>
      </div>
    </header>
  );
}

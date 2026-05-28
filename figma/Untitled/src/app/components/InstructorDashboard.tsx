import { useState } from 'react';
import { Plus, FileText, CheckCircle, Clock, TrendingUp } from 'lucide-react';
import { Link } from 'react-router';
import { DesignPatternTrace } from './DesignPatternTrace';

export function InstructorDashboard() {
  const [traces] = useState([
    {
      pattern: 'Abstract Factory',
      action: 'Created starter course content family',
      category: 'Creational' as const,
      timestamp: '2 min ago',
    },
    {
      pattern: 'Observer',
      action: 'Notification sent to 3 students',
      category: 'Behavioral' as const,
      timestamp: '5 min ago',
    },
    {
      pattern: 'Composite',
      action: 'Loaded course modules tree',
      category: 'Structural' as const,
      timestamp: '8 min ago',
    },
  ]);

  return (
    <div className="min-h-screen bg-background">
      <div className="pt-24 pb-12 px-6 mr-80">
        <div className="max-w-7xl mx-auto">
          {/* Stats Cards */}
          <div className="grid grid-cols-4 gap-4 mb-8">
            <div className="bg-card border border-border rounded-lg p-5">
              <div className="flex items-center justify-between mb-2">
                <p className="text-sm text-muted-foreground">Active Courses</p>
                <TrendingUp className="w-4 h-4 text-primary" />
              </div>
              <p className="text-3xl font-semibold">3</p>
            </div>
            <div className="bg-card border border-border rounded-lg p-5">
              <div className="flex items-center justify-between mb-2">
                <p className="text-sm text-muted-foreground">Pending Submissions</p>
                <Clock className="w-4 h-4 text-accent" />
              </div>
              <p className="text-3xl font-semibold">12</p>
            </div>
            <div className="bg-card border border-border rounded-lg p-5">
              <div className="flex items-center justify-between mb-2">
                <p className="text-sm text-muted-foreground">AI Reports Generated</p>
                <FileText className="w-4 h-4 text-emerald-600" />
              </div>
              <p className="text-3xl font-semibold">47</p>
            </div>
            <div className="bg-card border border-border rounded-lg p-5">
              <div className="flex items-center justify-between mb-2">
                <p className="text-sm text-muted-foreground">Graded This Week</p>
                <CheckCircle className="w-4 h-4 text-blue-600" />
              </div>
              <p className="text-3xl font-semibold">24</p>
            </div>
          </div>

          {/* Courses Section */}
          <div className="mb-8">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-xl font-semibold">Your Courses</h2>
              <Link
                to="/course-builder"
                className="flex items-center gap-2 px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:opacity-90 transition-opacity"
              >
                <Plus className="w-4 h-4" />
                Create Course
              </Link>
            </div>

            <div className="grid grid-cols-3 gap-4">
              <Link
                to="/course-builder"
                className="bg-card border border-border rounded-lg p-6 hover:border-primary transition-colors"
              >
                <div className="flex items-start justify-between mb-3">
                  <div className="w-12 h-12 rounded-lg bg-primary/10 flex items-center justify-center">
                    <span className="text-xl">🎨</span>
                  </div>
                  <span className="text-xs px-2 py-1 bg-emerald-100 text-emerald-700 rounded">
                    Active
                  </span>
                </div>
                <h3 className="font-semibold mb-1">Design Patterns CS501</h3>
                <p className="text-sm text-muted-foreground mb-3">Spring 2026</p>
                <div className="flex items-center gap-4 text-sm text-muted-foreground">
                  <span>42 students</span>
                  <span>•</span>
                  <span>8 modules</span>
                </div>
              </Link>

              <div className="bg-card border border-border rounded-lg p-6 opacity-60">
                <div className="flex items-start justify-between mb-3">
                  <div className="w-12 h-12 rounded-lg bg-accent/10 flex items-center justify-center">
                    <span className="text-xl">⚙️</span>
                  </div>
                  <span className="text-xs px-2 py-1 bg-gray-100 text-gray-600 rounded">
                    Archived
                  </span>
                </div>
                <h3 className="font-semibold mb-1">OOP Fundamentals</h3>
                <p className="text-sm text-muted-foreground mb-3">Fall 2025</p>
                <div className="flex items-center gap-4 text-sm text-muted-foreground">
                  <span>38 students</span>
                  <span>•</span>
                  <span>6 modules</span>
                </div>
              </div>

              <div className="bg-card border border-border rounded-lg p-6 opacity-60">
                <div className="flex items-start justify-between mb-3">
                  <div className="w-12 h-12 rounded-lg bg-blue-50 flex items-center justify-center">
                    <span className="text-xl">🏗️</span>
                  </div>
                  <span className="text-xs px-2 py-1 bg-gray-100 text-gray-600 rounded">
                    Archived
                  </span>
                </div>
                <h3 className="font-semibold mb-1">Software Architecture</h3>
                <p className="text-sm text-muted-foreground mb-3">Spring 2025</p>
                <div className="flex items-center gap-4 text-sm text-muted-foreground">
                  <span>35 students</span>
                  <span>•</span>
                  <span>10 modules</span>
                </div>
              </div>
            </div>
          </div>

          {/* Recent Submissions & Notifications */}
          <div className="grid grid-cols-2 gap-6">
            {/* Recent Submissions */}
            <div className="bg-card border border-border rounded-lg p-6">
              <h3 className="font-semibold mb-4">Recent Submissions</h3>
              <div className="space-y-3">
                {[
                  {
                    student: 'Sarah Chen',
                    assignment: 'Factory Pattern Implementation',
                    status: 'AI Analysis Complete',
                    time: '2 hours ago',
                    type: 'Code',
                  },
                  {
                    student: 'Marcus Johnson',
                    assignment: 'Observer Pattern Essay',
                    status: 'Pending Review',
                    time: '4 hours ago',
                    type: 'PDF',
                  },
                  {
                    student: 'Emily Rodriguez',
                    assignment: 'Decorator Pattern Demo',
                    status: 'AI Analysis Complete',
                    time: '5 hours ago',
                    type: 'Code',
                  },
                ].map((submission, i) => (
                  <Link
                    key={i}
                    to="/submission-review"
                    className="block p-3 rounded-lg hover:bg-muted transition-colors"
                  >
                    <div className="flex items-start justify-between mb-1">
                      <p className="font-medium text-sm">{submission.student}</p>
                      <span className="text-xs px-2 py-0.5 bg-blue-100 text-blue-700 rounded">
                        {submission.type}
                      </span>
                    </div>
                    <p className="text-sm text-muted-foreground mb-1">
                      {submission.assignment}
                    </p>
                    <div className="flex items-center gap-2 text-xs text-muted-foreground">
                      <span
                        className={
                          submission.status === 'AI Analysis Complete'
                            ? 'text-emerald-600'
                            : 'text-amber-600'
                        }
                      >
                        {submission.status}
                      </span>
                      <span>•</span>
                      <span>{submission.time}</span>
                    </div>
                  </Link>
                ))}
              </div>
            </div>

            {/* Notifications */}
            <div className="bg-card border border-border rounded-lg p-6">
              <h3 className="font-semibold mb-4">Notifications</h3>
              <div className="space-y-3">
                {[
                  {
                    message: 'AI analysis completed for 3 new submissions',
                    time: '10 min ago',
                    type: 'success',
                  },
                  {
                    message: 'Assignment deadline approaching: Strategy Pattern (2 days)',
                    time: '1 hour ago',
                    type: 'warning',
                  },
                  {
                    message: '5 students submitted Factory Pattern assignment',
                    time: '3 hours ago',
                    type: 'info',
                  },
                  {
                    message: 'New module published: Behavioral Patterns',
                    time: '1 day ago',
                    type: 'info',
                  },
                ].map((notif, i) => (
                  <div key={i} className="p-3 rounded-lg bg-muted/50">
                    <p className="text-sm mb-1">{notif.message}</p>
                    <p className="text-xs text-muted-foreground">{notif.time}</p>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>

      <DesignPatternTrace traces={traces} />
    </div>
  );
}

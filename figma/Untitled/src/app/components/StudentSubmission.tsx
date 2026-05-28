import { useState } from 'react';
import {
  Upload,
  FileText,
  Calendar,
  CheckCircle,
  ChevronRight,
  Download,
  Award,
} from 'lucide-react';
import { DesignPatternTrace } from './DesignPatternTrace';

export function StudentSubmission() {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [traces, setTraces] = useState([
    {
      pattern: 'Observer',
      action: 'Subscribed to assignment updates',
      category: 'Behavioral' as const,
      timestamp: 'Just now',
    },
  ]);

  const handleFileUpload = () => {
    setTraces((prev) => [
      {
        pattern: 'State',
        action: 'Submission: Draft → Submitted',
        category: 'Behavioral' as const,
        timestamp: 'Just now',
      },
      {
        pattern: 'Observer',
        action: 'Notification sent to instructor',
        category: 'Behavioral' as const,
        timestamp: 'Just now',
      },
      ...prev,
    ]);
  };

  return (
    <div className="min-h-screen bg-background">
      <div className="pt-24 pb-12 px-6 mr-80">
        <div className="max-w-5xl mx-auto">
          {/* Breadcrumb */}
          <div className="flex items-center gap-2 text-sm text-muted-foreground mb-6">
            <span>My Courses</span>
            <ChevronRight className="w-4 h-4" />
            <span>Design Patterns CS501</span>
            <ChevronRight className="w-4 h-4" />
            <span className="text-foreground">Assignment</span>
          </div>

          {/* Assignment Header */}
          <div className="bg-card border border-border rounded-lg p-6 mb-6">
            <div className="flex items-start justify-between mb-4">
              <div>
                <h1 className="text-2xl font-semibold mb-2">
                  Factory Pattern Implementation
                </h1>
                <p className="text-muted-foreground">Creational Patterns Module</p>
              </div>
              <div className="text-right">
                <p className="text-sm text-muted-foreground mb-1">Due Date</p>
                <div className="flex items-center gap-2 text-amber-600">
                  <Calendar className="w-4 h-4" />
                  <span className="font-medium">June 15, 2026</span>
                </div>
                <p className="text-xs text-muted-foreground mt-1">18 days remaining</p>
              </div>
            </div>

            <p className="text-sm leading-relaxed mb-4">
              Implement the Factory Method pattern to create a flexible payment processing
              system. Your solution should demonstrate proper use of the pattern with at least
              two concrete implementations (e.g., CreditCard and PayPal processors). Include
              unit tests to verify your implementation.
            </p>

            <div className="flex items-center gap-4">
              <div className="px-3 py-1.5 bg-primary/10 text-primary rounded-lg text-sm font-medium">
                100 points
              </div>
              <div className="px-3 py-1.5 bg-blue-100 text-blue-700 rounded-lg text-sm">
                Code Submission Required
              </div>
            </div>
          </div>

          <div className="grid grid-cols-3 gap-6">
            {/* Main Content */}
            <div className="col-span-2 space-y-6">
              {/* Grading Rubric */}
              <div className="bg-card border border-border rounded-lg p-6">
                <h3 className="font-semibold mb-4">Grading Rubric</h3>

                <div className="space-y-3">
                  {[
                    {
                      criteria: 'Correct Pattern Implementation',
                      points: 40,
                      description:
                        'Proper use of Factory Method pattern with abstract factory and concrete implementations',
                    },
                    {
                      criteria: 'Code Quality',
                      points: 25,
                      description: 'Clean, readable code with appropriate naming and structure',
                    },
                    {
                      criteria: 'Test Coverage',
                      points: 20,
                      description: 'Comprehensive unit tests covering main functionality',
                    },
                    {
                      criteria: 'Documentation',
                      points: 15,
                      description: 'Clear comments explaining design decisions',
                    },
                  ].map((item, i) => (
                    <div
                      key={i}
                      className="p-4 border border-border rounded-lg hover:border-primary/30 transition-colors"
                    >
                      <div className="flex items-start justify-between mb-2">
                        <p className="font-medium">{item.criteria}</p>
                        <span className="text-primary font-medium">{item.points} pts</span>
                      </div>
                      <p className="text-sm text-muted-foreground">{item.description}</p>
                    </div>
                  ))}
                </div>

                <div className="mt-4 pt-4 border-t border-border flex items-center justify-between">
                  <span className="font-medium">Total Points</span>
                  <span className="text-lg font-semibold">100 pts</span>
                </div>
              </div>

              {/* Submission Upload */}
              <div className="bg-card border border-border rounded-lg p-6">
                <h3 className="font-semibold mb-4">Submit Your Work</h3>

                <div className="border-2 border-dashed border-border rounded-lg p-8 text-center hover:border-primary/50 transition-colors cursor-pointer">
                  <Upload className="w-12 h-12 mx-auto mb-3 text-muted-foreground" />
                  <p className="font-medium mb-1">Drop your code files here</p>
                  <p className="text-sm text-muted-foreground mb-4">
                    or click to browse Java source
                  </p>
                  <input
                    type="file"
                    className="hidden"
                    id="file-upload"
                    onChange={(e) => {
                      if (e.target.files?.[0]) {
                        setSelectedFile(e.target.files[0]);
                      }
                    }}
                  />
                  <label
                    htmlFor="file-upload"
                    className="inline-flex items-center gap-2 px-4 py-2 bg-primary text-primary-foreground rounded-lg cursor-pointer hover:opacity-90 transition-opacity"
                  >
                    <FileText className="w-4 h-4" />
                    Choose File
                  </label>
                </div>

                {selectedFile && (
                  <div className="mt-4 p-3 bg-muted/30 rounded-lg flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <FileText className="w-5 h-5 text-primary" />
                      <div>
                        <p className="font-medium text-sm">{selectedFile.name}</p>
                        <p className="text-xs text-muted-foreground">
                          {(selectedFile.size / 1024).toFixed(1)} KB
                        </p>
                      </div>
                    </div>
                    <button className="text-sm text-destructive hover:underline">Remove</button>
                  </div>
                )}

                <button
                  onClick={handleFileUpload}
                  disabled={!selectedFile}
                  className="w-full mt-6 px-6 py-3 bg-primary text-primary-foreground rounded-lg hover:opacity-90 transition-opacity disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Submit Assignment
                </button>
              </div>

              {/* Previous Submission (Example) */}
              <div className="bg-card border border-emerald-200 rounded-lg p-6">
                <div className="flex items-start gap-3 mb-4">
                  <CheckCircle className="w-5 h-5 text-emerald-600 mt-0.5" />
                  <div className="flex-1">
                    <h3 className="font-semibold mb-1">Feedback Available</h3>
                    <p className="text-sm text-muted-foreground">
                      Your instructor has reviewed your previous assignment
                    </p>
                  </div>
                </div>

                <div className="p-4 bg-muted/30 rounded-lg mb-4">
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-sm font-medium">Observer Pattern Essay</span>
                    <span className="text-sm text-muted-foreground">May 20, 2026</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Award className="w-5 h-5 text-primary" />
                    <span className="text-lg font-semibold">92 / 100</span>
                  </div>
                </div>

                <button className="w-full flex items-center justify-center gap-2 px-4 py-2 border border-border rounded-lg hover:bg-muted transition-colors">
                  <Download className="w-4 h-4" />
                  View Detailed Feedback
                </button>
              </div>
            </div>

            {/* Sidebar */}
            <div className="space-y-6">
              {/* Submission Status */}
              <div className="bg-card border border-border rounded-lg p-6">
                <h3 className="font-semibold mb-4">Submission Status</h3>

                <div className="space-y-3">
                  <div className="flex items-center gap-3">
                    <div className="w-8 h-8 rounded-full bg-emerald-100 flex items-center justify-center">
                      <CheckCircle className="w-5 h-5 text-emerald-600" />
                    </div>
                    <div>
                      <p className="text-sm font-medium">Not Submitted</p>
                      <p className="text-xs text-muted-foreground">Ready to submit</p>
                    </div>
                  </div>
                </div>

                <div className="mt-4 pt-4 border-t border-border">
                  <div className="text-sm space-y-2">
                    <div className="flex justify-between">
                      <span className="text-muted-foreground">Attempts</span>
                      <span className="font-medium">0 / 3</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-muted-foreground">Last Modified</span>
                      <span className="font-medium">—</span>
                    </div>
                  </div>
                </div>
              </div>

              {/* Resources */}
              <div className="bg-card border border-border rounded-lg p-6">
                <h3 className="font-semibold mb-4">Resources</h3>

                <div className="space-y-2">
                  {[
                    'Factory Pattern Lecture Slides',
                    'Example Code Repository',
                    'Unit Testing Guide',
                    'Java Style Guidelines',
                  ].map((resource, i) => (
                    <button
                      key={i}
                      className="w-full text-left p-3 rounded-lg hover:bg-muted transition-colors flex items-center gap-2"
                    >
                      <FileText className="w-4 h-4 text-primary" />
                      <span className="text-sm">{resource}</span>
                    </button>
                  ))}
                </div>
              </div>

              {/* Help */}
              <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                <h4 className="font-medium text-sm mb-2 text-blue-900">Need Help?</h4>
                <p className="text-xs text-blue-800 mb-3">
                  Office hours: Mon/Wed 2-4 PM or post in the course forum
                </p>
                <button className="text-xs text-primary font-medium hover:underline">
                  Contact Instructor
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <DesignPatternTrace traces={traces} />
    </div>
  );
}

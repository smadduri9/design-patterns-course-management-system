import { useState } from 'react';
import { Plus, ChevronRight, FileText, Code, Trash2, Save } from 'lucide-react';
import { DesignPatternTrace } from './DesignPatternTrace';

export function CourseBuilder() {
  const [submissionType, setSubmissionType] = useState<'pdf' | 'code'>('pdf');
  const [traces, setTraces] = useState([
    {
      pattern: 'Composite',
      action: 'Module tree structure loaded',
      category: 'Structural' as const,
      timestamp: 'Just now',
    },
  ]);

  const handleSaveAssignment = () => {
    setTraces((prev) => [
      {
        pattern: 'Builder',
        action: 'Built Rubric with 4 criteria',
        category: 'Creational' as const,
        timestamp: 'Just now',
      },
      {
        pattern: 'Factory Method',
        action: submissionType === 'code' ? 'Created CodeAssignment' : 'Created PDFAssignment',
        category: 'Creational' as const,
        timestamp: 'Just now',
      },
      {
        pattern: 'Composite',
        action: 'Added Assignment to Module',
        category: 'Structural' as const,
        timestamp: 'Just now',
      },
      ...prev,
    ]);
  };

  return (
    <div className="min-h-screen bg-background">
      <div className="pt-24 pb-12 px-6 mr-80">
        <div className="max-w-7xl mx-auto">
          {/* Course Header */}
          <div className="mb-8">
            <div className="flex items-center gap-2 text-sm text-muted-foreground mb-2">
              <span>Courses</span>
              <ChevronRight className="w-4 h-4" />
              <span className="text-foreground">Design Patterns CS501</span>
            </div>
            <h1 className="text-2xl font-semibold">Course Builder</h1>
          </div>

          <div className="grid grid-cols-3 gap-6">
            {/* Module List */}
            <div className="bg-card border border-border rounded-lg p-6">
              <div className="flex items-center justify-between mb-4">
                <h3 className="font-semibold">Modules</h3>
                <button className="p-1.5 hover:bg-muted rounded transition-colors">
                  <Plus className="w-4 h-4" />
                </button>
              </div>

              <div className="space-y-2">
                {[
                  { name: 'Introduction to Design Patterns', count: 3 },
                  { name: 'Creational Patterns', count: 5 },
                  { name: 'Structural Patterns', count: 7, active: true },
                  { name: 'Behavioral Patterns', count: 8 },
                  { name: 'Advanced Topics', count: 2 },
                ].map((module, i) => (
                  <div
                    key={i}
                    className={`p-3 rounded-lg cursor-pointer transition-colors ${
                      module.active
                        ? 'bg-primary text-primary-foreground'
                        : 'hover:bg-muted'
                    }`}
                  >
                    <p className="text-sm font-medium mb-0.5">{module.name}</p>
                    <p
                      className={`text-xs ${
                        module.active ? 'opacity-90' : 'text-muted-foreground'
                      }`}
                    >
                      {module.count} assignments
                    </p>
                  </div>
                ))}
              </div>
            </div>

            {/* Assignment Creation Form */}
            <div className="col-span-2 space-y-6">
              {/* Basic Info */}
              <div className="bg-card border border-border rounded-lg p-6">
                <h3 className="font-semibold mb-4">Assignment Details</h3>

                <div className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium mb-2">
                      Assignment Title
                    </label>
                    <input
                      type="text"
                      placeholder="e.g., Adapter Pattern Implementation"
                      className="w-full px-3 py-2 bg-input-background border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-ring"
                      defaultValue="Adapter Pattern Implementation"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium mb-2">Description</label>
                    <textarea
                      rows={3}
                      placeholder="Assignment description and instructions..."
                      className="w-full px-3 py-2 bg-input-background border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-ring"
                      defaultValue="Implement the Adapter pattern to integrate a legacy payment system with a modern e-commerce platform. Your solution should demonstrate proper use of the pattern and include unit tests."
                    />
                  </div>

                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium mb-2">Due Date</label>
                      <input
                        type="date"
                        className="w-full px-3 py-2 bg-input-background border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-ring"
                        defaultValue="2026-06-15"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium mb-2">Points</label>
                      <input
                        type="number"
                        className="w-full px-3 py-2 bg-input-background border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-ring"
                        defaultValue="100"
                      />
                    </div>
                  </div>
                </div>
              </div>

              {/* Submission Type */}
              <div className="bg-card border border-border rounded-lg p-6">
                <h3 className="font-semibold mb-4">Submission Type</h3>

                <div className="grid grid-cols-2 gap-3">
                  <button
                    onClick={() => setSubmissionType('pdf')}
                    className={`p-4 rounded-lg border-2 transition-all ${
                      submissionType === 'pdf'
                        ? 'border-primary bg-primary/5'
                        : 'border-border hover:border-muted-foreground'
                    }`}
                  >
                    <FileText className="w-6 h-6 mb-2 mx-auto" />
                    <p className="font-medium text-sm">PDF / Text</p>
                    <p className="text-xs text-muted-foreground mt-1">
                      Essays, reports, documents
                    </p>
                  </button>

                  <button
                    onClick={() => setSubmissionType('code')}
                    className={`p-4 rounded-lg border-2 transition-all ${
                      submissionType === 'code'
                        ? 'border-primary bg-primary/5'
                        : 'border-border hover:border-muted-foreground'
                    }`}
                  >
                    <Code className="w-6 h-6 mb-2 mx-auto" />
                    <p className="font-medium text-sm">Code</p>
                    <p className="text-xs text-muted-foreground mt-1">
                      Programming assignments
                    </p>
                  </button>
                </div>

                {submissionType === 'code' && (
                  <div className="mt-4 p-4 bg-muted/30 rounded-lg">
                    <label className="block text-sm font-medium mb-2">
                      Test Cases (Optional)
                    </label>
                    <textarea
                      rows={4}
                      placeholder="Enter test cases for automated code validation..."
                      className="w-full px-3 py-2 bg-input-background border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-ring font-mono text-sm"
                      defaultValue={`test_adapter_integration()
test_legacy_system_compatibility()
test_error_handling()`}
                    />
                  </div>
                )}
              </div>

              {/* Rubric Builder */}
              <div className="bg-card border border-border rounded-lg p-6">
                <div className="flex items-center justify-between mb-4">
                  <h3 className="font-semibold">Grading Rubric</h3>
                  <button className="flex items-center gap-2 px-3 py-1.5 text-sm border border-border rounded-lg hover:bg-muted transition-colors">
                    <Plus className="w-4 h-4" />
                    Add Criteria
                  </button>
                </div>

                <div className="space-y-3">
                  {[
                    {
                      criteria: 'Correct Pattern Implementation',
                      points: 40,
                      description: 'Proper use of Adapter pattern structure',
                    },
                    {
                      criteria: 'Code Quality',
                      points: 25,
                      description: 'Clean, readable, well-documented code',
                    },
                    {
                      criteria: 'Test Coverage',
                      points: 20,
                      description: 'Comprehensive unit tests',
                    },
                    {
                      criteria: 'Documentation',
                      points: 15,
                      description: 'Clear explanation of design decisions',
                    },
                  ].map((item, i) => (
                    <div
                      key={i}
                      className="p-3 border border-border rounded-lg hover:border-primary/50 transition-colors"
                    >
                      <div className="flex items-start justify-between mb-1">
                        <p className="font-medium text-sm">{item.criteria}</p>
                        <div className="flex items-center gap-2">
                          <span className="text-sm font-medium text-primary">
                            {item.points} pts
                          </span>
                          <button className="p-1 hover:bg-destructive/10 rounded transition-colors">
                            <Trash2 className="w-3.5 h-3.5 text-destructive" />
                          </button>
                        </div>
                      </div>
                      <p className="text-xs text-muted-foreground">{item.description}</p>
                    </div>
                  ))}
                </div>

                <div className="mt-4 pt-4 border-t border-border flex items-center justify-between">
                  <span className="text-sm text-muted-foreground">Total Points</span>
                  <span className="font-semibold">100 pts</span>
                </div>
              </div>

              {/* Save Button */}
              <button
                onClick={handleSaveAssignment}
                className="w-full flex items-center justify-center gap-2 px-6 py-3 bg-primary text-primary-foreground rounded-lg hover:opacity-90 transition-opacity"
              >
                <Save className="w-5 h-5" />
                Save Assignment
              </button>
            </div>
          </div>
        </div>
      </div>

      <DesignPatternTrace traces={traces} />
    </div>
  );
}

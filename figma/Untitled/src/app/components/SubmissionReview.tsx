import { useState } from 'react';
import {
  FileText,
  User,
  Calendar,
  CheckCircle,
  AlertCircle,
  Send,
  Code,
  ChevronRight,
} from 'lucide-react';
import { DesignPatternTrace } from './DesignPatternTrace';

export function SubmissionReview() {
  const [feedback, setFeedback] = useState('');
  const [traces, setTraces] = useState([
    {
      pattern: 'Strategy',
      action: 'Selected CodeAnalysisStrategy',
      category: 'Behavioral' as const,
      timestamp: '2 min ago',
    },
    {
      pattern: 'Adapter',
      action: 'Used MockAIServiceAdapter for AI analysis',
      category: 'Structural' as const,
      timestamp: '2 min ago',
    },
    {
      pattern: 'State',
      action: 'Submission: Submitted → Processing → Analyzed',
      category: 'Behavioral' as const,
      timestamp: '5 min ago',
    },
  ]);

  const handleSendFeedback = () => {
    setTraces((prev) => [
      {
        pattern: 'Observer',
        action: 'Notification sent to student',
        category: 'Behavioral' as const,
        timestamp: 'Just now',
      },
      {
        pattern: 'State',
        action: 'Submission: Analyzed → Graded',
        category: 'Behavioral' as const,
        timestamp: 'Just now',
      },
      ...prev,
    ]);
  };

  return (
    <div className="min-h-screen bg-background">
      <div className="pt-24 pb-12 px-6 mr-80">
        <div className="max-w-7xl mx-auto">
          {/* Breadcrumb */}
          <div className="flex items-center gap-2 text-sm text-muted-foreground mb-6">
            <span>Dashboard</span>
            <ChevronRight className="w-4 h-4" />
            <span>Submissions</span>
            <ChevronRight className="w-4 h-4" />
            <span className="text-foreground">Review</span>
          </div>

          {/* Submission Header */}
          <div className="bg-card border border-border rounded-lg p-6 mb-6">
            <div className="flex items-start justify-between mb-4">
              <div>
                <h1 className="text-2xl font-semibold mb-2">
                  Factory Pattern Implementation
                </h1>
                <div className="flex items-center gap-4 text-sm text-muted-foreground">
                  <div className="flex items-center gap-2">
                    <User className="w-4 h-4" />
                    <span>Sarah Chen</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Calendar className="w-4 h-4" />
                    <span>Submitted May 28, 2026 at 2:15 PM</span>
                  </div>
                </div>
              </div>
              <span className="px-3 py-1.5 bg-blue-100 text-blue-700 rounded-lg text-sm font-medium flex items-center gap-2">
                <Code className="w-4 h-4" />
                Code Submission
              </span>
            </div>

            <div className="grid grid-cols-3 gap-4">
              <div className="p-3 bg-muted/30 rounded-lg">
                <p className="text-xs text-muted-foreground mb-1">Module</p>
                <p className="text-sm font-medium">Creational Patterns</p>
              </div>
              <div className="p-3 bg-muted/30 rounded-lg">
                <p className="text-xs text-muted-foreground mb-1">Total Points</p>
                <p className="text-sm font-medium">100 points</p>
              </div>
              <div className="p-3 bg-muted/30 rounded-lg">
                <p className="text-xs text-muted-foreground mb-1">Status</p>
                <p className="text-sm font-medium text-emerald-600">AI Analysis Complete</p>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-6">
            {/* Left Column */}
            <div className="space-y-6">
              {/* Student Code */}
              <div className="bg-card border border-border rounded-lg p-6">
                <h3 className="font-semibold mb-4">Submitted Code</h3>
                <div className="bg-muted/30 rounded-lg p-4 font-mono text-sm overflow-x-auto">
                  <pre className="text-xs leading-relaxed">
                    {`// PaymentProcessorFactory.java
public interface PaymentProcessor {
    void processPayment(double amount);
}

public class CreditCardProcessor implements PaymentProcessor {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing $" + amount + " via Credit Card");
    }
}

public class PayPalProcessor implements PaymentProcessor {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing $" + amount + " via PayPal");
    }
}

public abstract class PaymentProcessorFactory {
    public abstract PaymentProcessor createProcessor();
}

public class CreditCardFactory extends PaymentProcessorFactory {
    @Override
    public PaymentProcessor createProcessor() {
        return new CreditCardProcessor();
    }
}`}
                  </pre>
                </div>
              </div>

              {/* Test Results */}
              <div className="bg-card border border-border rounded-lg p-6">
                <h3 className="font-semibold mb-4">Test Results</h3>
                <div className="space-y-2">
                  {[
                    { name: 'test_factory_method_pattern()', passed: true },
                    { name: 'test_credit_card_processor()', passed: true },
                    { name: 'test_paypal_processor()', passed: true },
                    { name: 'test_multiple_processors()', passed: true },
                    { name: 'test_edge_cases()', passed: false },
                  ].map((test, i) => (
                    <div
                      key={i}
                      className="flex items-center justify-between p-3 rounded-lg bg-muted/30"
                    >
                      <span className="font-mono text-sm">{test.name}</span>
                      {test.passed ? (
                        <CheckCircle className="w-5 h-5 text-emerald-600" />
                      ) : (
                        <AlertCircle className="w-5 h-5 text-amber-600" />
                      )}
                    </div>
                  ))}
                </div>
                <div className="mt-4 pt-4 border-t border-border">
                  <p className="text-sm">
                    <span className="font-medium">4 of 5</span> tests passed
                  </p>
                </div>
              </div>
            </div>

            {/* Right Column */}
            <div className="space-y-6">
              {/* AI Analysis Report */}
              <div className="bg-card border border-border rounded-lg p-6">
                <h3 className="font-semibold mb-4">AI Analysis Report</h3>

                <div className="space-y-4">
                  <div>
                    <div className="flex items-center gap-2 mb-2">
                      <CheckCircle className="w-4 h-4 text-emerald-600" />
                      <h4 className="font-medium text-sm">Pattern Implementation</h4>
                    </div>
                    <p className="text-sm text-muted-foreground">
                      Student correctly implements the Factory Method pattern with proper
                      abstraction. The factory hierarchy is well-structured with clear
                      separation of concerns.
                    </p>
                  </div>

                  <div>
                    <div className="flex items-center gap-2 mb-2">
                      <CheckCircle className="w-4 h-4 text-emerald-600" />
                      <h4 className="font-medium text-sm">Code Quality</h4>
                    </div>
                    <p className="text-sm text-muted-foreground">
                      Clean, readable code with appropriate naming conventions. Good use of
                      interfaces and inheritance.
                    </p>
                  </div>

                  <div>
                    <div className="flex items-center gap-2 mb-2">
                      <AlertCircle className="w-4 h-4 text-amber-600" />
                      <h4 className="font-medium text-sm">Areas for Improvement</h4>
                    </div>
                    <p className="text-sm text-muted-foreground">
                      Edge case handling could be improved. Consider adding validation for
                      negative amounts and null checks.
                    </p>
                  </div>
                </div>

                <div className="mt-4 pt-4 border-t border-border">
                  <p className="text-sm font-medium mb-2">AI Suggested Score</p>
                  <div className="flex items-baseline gap-2">
                    <span className="text-2xl font-semibold text-primary">85</span>
                    <span className="text-muted-foreground">/ 100 points</span>
                  </div>
                </div>
              </div>

              {/* Rubric Match */}
              <div className="bg-card border border-border rounded-lg p-6">
                <h3 className="font-semibold mb-4">Rubric Assessment</h3>

                <div className="space-y-3">
                  {[
                    {
                      criteria: 'Correct Pattern Implementation',
                      earned: 38,
                      total: 40,
                      status: 'excellent',
                    },
                    { criteria: 'Code Quality', earned: 23, total: 25, status: 'good' },
                    { criteria: 'Test Coverage', earned: 16, total: 20, status: 'fair' },
                    { criteria: 'Documentation', earned: 8, total: 15, status: 'needs-work' },
                  ].map((item, i) => (
                    <div key={i} className="space-y-2">
                      <div className="flex items-center justify-between text-sm">
                        <span className="font-medium">{item.criteria}</span>
                        <span className="text-muted-foreground">
                          {item.earned} / {item.total} pts
                        </span>
                      </div>
                      <div className="h-2 bg-muted rounded-full overflow-hidden">
                        <div
                          className={`h-full ${
                            item.status === 'excellent'
                              ? 'bg-emerald-500'
                              : item.status === 'good'
                                ? 'bg-blue-500'
                                : item.status === 'fair'
                                  ? 'bg-amber-500'
                                  : 'bg-red-500'
                          }`}
                          style={{ width: `${(item.earned / item.total) * 100}%` }}
                        ></div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              {/* Instructor Feedback */}
              <div className="bg-card border border-border rounded-lg p-6">
                <h3 className="font-semibold mb-4">Your Feedback</h3>

                <textarea
                  rows={6}
                  placeholder="Add your feedback and final comments for the student..."
                  className="w-full px-3 py-2 bg-input-background border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-ring mb-4"
                  value={feedback}
                  onChange={(e) => setFeedback(e.target.value)}
                  defaultValue="Great work on implementing the Factory Method pattern! Your code structure is clean and follows good OOP principles. Consider adding more documentation to explain your design decisions, and implement better error handling for edge cases."
                />

                <div className="mb-4">
                  <label className="block text-sm font-medium mb-2">Final Score</label>
                  <input
                    type="number"
                    className="w-full px-3 py-2 bg-input-background border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-ring"
                    defaultValue="85"
                    max="100"
                  />
                </div>

                <button
                  onClick={handleSendFeedback}
                  className="w-full flex items-center justify-center gap-2 px-6 py-3 bg-primary text-primary-foreground rounded-lg hover:opacity-90 transition-opacity"
                >
                  <Send className="w-5 h-5" />
                  Send Final Feedback
                </button>

                <div className="mt-4 p-3 bg-blue-50 border border-blue-200 rounded-lg">
                  <p className="text-xs text-blue-900">
                    <strong>Notification Preview:</strong> Sarah Chen will receive an email and
                    in-app notification that feedback is available for this assignment.
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <DesignPatternTrace traces={traces} />
    </div>
  );
}

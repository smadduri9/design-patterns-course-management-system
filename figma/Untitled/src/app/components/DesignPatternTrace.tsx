import { Layers, Package, Workflow } from 'lucide-react';

interface TraceItem {
  pattern: string;
  action: string;
  category: 'Creational' | 'Structural' | 'Behavioral';
  timestamp: string;
}

interface DesignPatternTraceProps {
  traces: TraceItem[];
}

const categoryColors = {
  Creational: 'bg-emerald-100 text-emerald-700 border-emerald-300',
  Structural: 'bg-blue-100 text-blue-700 border-blue-300',
  Behavioral: 'bg-purple-100 text-purple-700 border-purple-300',
};

const categoryIcons = {
  Creational: Package,
  Structural: Layers,
  Behavioral: Workflow,
};

export function DesignPatternTrace({ traces }: DesignPatternTraceProps) {
  return (
    <div className="fixed right-0 top-16 bottom-0 w-80 bg-card border-l border-border overflow-y-auto">
      <div className="p-4 border-b border-border bg-muted/30">
        <h3 className="font-medium flex items-center gap-2">
          <Layers className="w-4 h-4" />
          Design Pattern Trace
        </h3>
        <p className="text-sm text-muted-foreground mt-1">
          Patterns used in this workflow
        </p>
      </div>

      <div className="p-4 space-y-3">
        {traces.length === 0 ? (
          <div className="text-center text-sm text-muted-foreground py-8">
            No patterns traced yet
          </div>
        ) : (
          traces.map((trace, index) => {
            const Icon = categoryIcons[trace.category];
            return (
              <div
                key={index}
                className={`p-3 rounded-lg border ${categoryColors[trace.category]} transition-all hover:shadow-sm`}
              >
                <div className="flex items-start gap-2">
                  <Icon className="w-4 h-4 mt-0.5 flex-shrink-0" />
                  <div className="flex-1 min-w-0">
                    <div className="font-medium text-sm">{trace.pattern}</div>
                    <div className="text-xs mt-1 opacity-90">{trace.action}</div>
                    <div className="text-xs mt-1 opacity-70">{trace.timestamp}</div>
                  </div>
                </div>
              </div>
            );
          })
        )}
      </div>

      <div className="p-4 border-t border-border bg-muted/20">
        <div className="text-xs font-medium mb-2">Pattern Categories</div>
        <div className="space-y-2">
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 rounded-full bg-emerald-500"></div>
            <span className="text-xs">Creational</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 rounded-full bg-blue-500"></div>
            <span className="text-xs">Structural</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 rounded-full bg-purple-500"></div>
            <span className="text-xs">Behavioral</span>
          </div>
        </div>
      </div>
    </div>
  );
}

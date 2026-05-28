# design-patterns-course-management-system

## Visual Design Guidelines

### Design Stance

**Swiss / Academic Clean** — This interface adopts a precise, functional aesthetic appropriate for an academic learning management system. The design prioritizes clarity, hierarchy, and usability with minimal ornamentation.

### Typography

- **Display/Headings**: Work Sans — Clean geometric sans-serif for headers and UI labels
- **Body Text**: Inter — Highly readable sans-serif for content and descriptions  
- **Code/Data**: JetBrains Mono — Monospace for code snippets and technical content

### Color Palette

**Primary Colors:**
- Primary: `#4f46e5` (Indigo) — Interactive elements, CTAs
- Accent: `#8b5cf6` (Purple) — Highlights and secondary actions
- Background: `#f8f9fb` — Page background
- Card: `#ffffff` — Card and panel backgrounds

**Semantic Colors:**
- Success/Emerald: `#10b981` — Creational patterns, success states
- Blue: `#3b82f6` — Structural patterns, information
- Purple: `#8b5cf6` — Behavioral patterns, emphasis
- Destructive: `#ef4444` — Errors and destructive actions

**Pattern Category Colors:**
- Creational: Emerald (`bg-emerald-100 text-emerald-700`)
- Structural: Blue (`bg-blue-100 text-blue-700`)
- Behavioral: Purple (`bg-purple-100 text-purple-700`)

### Layout

**Grid System:**
- Consistent spacing scale using Tailwind's default spacing
- Main content: max-width 7xl with padding
- Right sidebar: Fixed 320px (80 rem units) for Design Pattern Trace Panel
- Card-based layout with consistent 24px (6 rem) padding

**Screen Structure:**
- Fixed header: 64px height
- Content area: Padding top 96px to account for fixed header
- Right margin: 320px to accommodate fixed trace panel

### Components

**Cards:**
- White background with subtle border
- Rounded corners (0.5rem)
- Hover states with border color transition
- Consistent internal padding (24px)

**Buttons:**
- Primary: Indigo background with white text
- Rounded corners matching card radius
- Clear hover states (opacity change)
- Icon + text combinations for clarity

**Design Pattern Trace Panel:**
- Fixed right sidebar (320px wide)
- Color-coded by pattern category
- Chronological trace items with timestamps
- Category legend at bottom

### Icons

All icons use Lucide React library at consistent sizes:
- Small: 16px (w-4 h-4)
- Medium: 20px (w-5 h-5)
- Large: 24px (w-6 h-6)

### Navigation

React Router handles routing between screens:
- `/` — Instructor Dashboard
- `/course-builder` — Course & Assignment Creation
- `/submission-review` — Submission Review
- `/student` — Student Submission View

### Data & Content

Uses realistic academic content:
- Actual course names and module titles
- Real design pattern examples
- Contextual notification messages
- Meaningful test case names

### Accessibility

- Semantic HTML structure
- Sufficient color contrast for text
- Interactive elements with clear hover/focus states
- Descriptive icon labels

### Responsive Behavior

Designed primarily for desktop use (1440px+). The Design Pattern Trace Panel is a key feature and remains visible on wider viewports.

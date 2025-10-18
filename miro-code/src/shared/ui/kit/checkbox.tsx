import * as React from "react"
import * as CheckboxPrimitive from "@radix-ui/react-checkbox"
import { CheckIcon } from "lucide-react"

import { cn } from "@/shared/lib/css"

function Checkbox({
                      className,
                      ...props
                  }: React.ComponentProps<typeof CheckboxPrimitive.Root>) {
    return (
        <CheckboxPrimitive.Root
            data-slot="checkbox"
            className={cn(
                "peer border-input dark:bg-input/30 data-[state=checked]:bg-primary data-[state=checked]:text-primary-foreground data-[state=checked]:border-primary focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] dark:data-[state=checked]:bg-primary dark:data-[state=checked]:border-primary aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive",
                "flex h-4 w-4 shrink-0 items-center justify-center rounded-[4px] border shadow-xs transition-all",
                "disabled:pointer-events-none disabled:cursor-not-allowed disabled:opacity-50",
                "data-[state=checked]:disabled:bg-muted data-[state=checked]:disabled:text-muted-foreground data-[state=checked]:disabled:border-border",
                className
            )}
            {...props}
        >
            <CheckboxPrimitive.Indicator
                data-slot="checkbox-indicator"
                className="flex items-center justify-center text-current"
            >
                <CheckIcon className="size-3" />
            </CheckboxPrimitive.Indicator>
        </CheckboxPrimitive.Root>
    )
}

export { Checkbox }
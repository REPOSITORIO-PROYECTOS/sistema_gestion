"use client";

import { type DialogProps } from "@radix-ui/react-dialog";
import { Command as CommandPrimitive } from "cmdk";
import { Search } from "lucide-react";
import * as React from "react";

import { cn } from "@/lib/utils";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogTitle,
} from "@/components/ui/dialog";

const Command = React.forwardRef<
  //@ts-ignore
  React.ElementRef<typeof CommandPrimitive>,
  //@ts-ignore
  React.ComponentPropsWithoutRef<typeof CommandPrimitive>
//@ts-ignore
>(({ className, ...props }, ref) => (
  //@ts-ignore
  <CommandPrimitive
    //@ts-ignore
    ref={ref}
    className={cn(
      "flex h-full w-full flex-col overflow-hidden rounded-lg bg-popover text-popover-foreground",
      className,
    )}
    {...props}
  />
));
Command.displayName = CommandPrimitive.displayName;

const CommandDialog = ({ children, ...props }: DialogProps) => {
  return (
    <Dialog {...props}>
      <DialogTitle></DialogTitle> {/* Temporary fix to silence console warning */}
      <DialogDescription></DialogDescription> {/* Temporary fix to silence console warning */}
      <DialogContent className="overflow-hidden p-0 sm:max-w-lg [&>button:last-child]:hidden">
        {
          // @ts-ignore
          <Command className="[&_[cmdk-group-heading]]:px-2 [&_[cmdk-group-heading]]:font-medium [&_[cmdk-group-heading]]:text-muted-foreground [&_[cmdk-group]]:px-2 [&_[cmdk-input]]:h-12 [&_[cmdk-item]]:px-3 [&_[cmdk-item]]:py-2">
            {children}
          </Command>}
      </DialogContent>
    </Dialog>
  );
};

const CommandInput = React.forwardRef<
  //@ts-ignore
  React.ElementRef<typeof CommandPrimitive.Input>,
  //@ts-ignore
  React.ComponentPropsWithoutRef<typeof CommandPrimitive.Input>
//@ts-ignore
>(({ className, ...props }, ref) => (
  <div className="flex items-center border-b border-input px-5" cmdk-input-wrapper="">
    <Search size={20} strokeWidth={2} className="me-3 text-muted-foreground/80" />
    {
      // @ts-ignore
      <CommandPrimitive.Input
        //@ts-ignore
        ref={ref}
        className={cn(
          "flex h-10 w-full rounded-lg bg-transparent py-3 text-sm outline-hidden placeholder:text-muted-foreground/70 disabled:cursor-not-allowed disabled:opacity-50",
          className,
        )}
        {...props}
      />}
  </div>
));

CommandInput.displayName = CommandPrimitive.Input.displayName;

const CommandList = React.forwardRef<
  //@ts-ignore
  React.ElementRef<typeof CommandPrimitive.List>,
  //@ts-ignore
  React.ComponentPropsWithoutRef<typeof CommandPrimitive.List>
//@ts-ignore
>(({ className, ...props }, ref) => (
  //@ts-ignore
  <CommandPrimitive.List
    //@ts-ignore
    ref={ref}
    className={cn("max-h-80 overflow-y-auto overflow-x-hidden", className)}
    {...props}
  />
));

CommandList.displayName = CommandPrimitive.List.displayName;

const CommandEmpty = React.forwardRef<
  //@ts-ignore
  React.ElementRef<typeof CommandPrimitive.Empty>,
  //@ts-ignore
  React.ComponentPropsWithoutRef<typeof CommandPrimitive.Empty>
>((props, ref) => (
  //@ts-ignore
  <CommandPrimitive.Empty ref={ref} className="py-6 text-center text-sm" {...props} />
));

CommandEmpty.displayName = CommandPrimitive.Empty.displayName;

const CommandGroup = React.forwardRef<
  //@ts-ignore
  React.ElementRef<typeof CommandPrimitive.Group>,
  //@ts-ignore
  React.ComponentPropsWithoutRef<typeof CommandPrimitive.Group>
//@ts-ignore
>(({ className, ...props }, ref) => (
  //@ts-ignore
  <CommandPrimitive.Group
    //@ts-ignore
    ref={ref}
    className={cn(
      "overflow-hidden p-2 text-foreground [&_[cmdk-group-heading]]:px-3 [&_[cmdk-group-heading]]:py-2 [&_[cmdk-group-heading]]:text-xs [&_[cmdk-group-heading]]:font-medium [&_[cmdk-group-heading]]:text-muted-foreground",
      className,
    )}
    {...props}
  />
));

CommandGroup.displayName = CommandPrimitive.Group.displayName;

const CommandSeparator = React.forwardRef<
  //@ts-ignore
  React.ElementRef<typeof CommandPrimitive.Separator>,
  //@ts-ignore
  React.ComponentPropsWithoutRef<typeof CommandPrimitive.Separator>
//@ts-ignore
>(({ className, ...props }, ref) => (
  //@ts-ignore
  <CommandPrimitive.Separator
    //@ts-ignore
    ref={ref}
    className={cn("-mx-1 h-px bg-border", className)}
    {...props}
  />
));
CommandSeparator.displayName = CommandPrimitive.Separator.displayName;

const CommandItem = React.forwardRef<
  //@ts-ignore
  React.ElementRef<typeof CommandPrimitive.Item>,
  //@ts-ignore
  React.ComponentPropsWithoutRef<typeof CommandPrimitive.Item>
//@ts-ignore
>(({ className, ...props }, ref) => (
  //@ts-ignore
  <CommandPrimitive.Item
    //@ts-ignore
    ref={ref}
    className={cn(
      "relative flex cursor-default select-none items-center gap-3 rounded-md px-2 py-1.5 text-sm outline-hidden data-[disabled=true]:pointer-events-none data-[selected=true]:bg-accent data-[selected=true]:text-accent-foreground data-[disabled=true]:opacity-50 [&_svg]:pointer-events-none [&_svg]:shrink-0",
      className,
    )}
    {...props}
  />
));

CommandItem.displayName = CommandPrimitive.Item.displayName;

const CommandShortcut = ({ className, ...props }: React.HTMLAttributes<HTMLSpanElement>) => {
  return (
    <kbd
      className={cn(
        "-me-1 ms-auto inline-flex h-5 max-h-full items-center rounded border border-border bg-background px-1 font-[inherit] text-[0.625rem] font-medium text-muted-foreground/70",
        className,
      )}
      {...props}
    />
  );
};
CommandShortcut.displayName = "CommandShortcut";

export {
  Command,
  CommandDialog,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
  CommandSeparator,
  CommandShortcut,
};

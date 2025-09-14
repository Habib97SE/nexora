import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Tooltip, TooltipContent, TooltipTrigger } from "@/components/ui/tooltip";
import { Sheet, SheetContent, SheetTrigger } from "@/components/ui/sheet";
import { CiHeart, CiSearch } from "react-icons/ci";
import { IoPersonOutline } from "react-icons/io5";
import { SlBag } from "react-icons/sl";
import { HiMenuAlt2 } from "react-icons/hi";
import { GermanyFlag, USFlag } from "@/components/ui/flags";
import Link from "next/link";

export default function Home() {
  return (
    <div>
      <header>
        {/* Main Header */}
        <div className="main-header border-b">
          <div className="container mx-auto px-4 lg:px-10">
            <div className="flex items-center justify-between py-4">
              
              {/* Left Section - Currency & Language (Hidden on mobile) */}
              <div className="hidden xl:flex xl:w-5/12 items-center gap-4">
                <div className="flex items-center gap-3">
                  {/* Currency Selector */}
                  <div className="tf-currencies">
                    <Select defaultValue="USD">
                      <SelectTrigger className="w-fit border-none shadow-none hover:bg-gray-50 transition-colors duration-200">
                        <SelectValue placeholder="Select a currency" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="EUR">
                          <div className="flex items-center gap-2">
                            <GermanyFlag />
                            <span>EUR</span>
                          </div>
                        </SelectItem>
                        <SelectItem value="USD">
                          <div className="flex items-center gap-2">
                            <USFlag />
                            <span>USD</span>
                          </div>
                        </SelectItem>
                      </SelectContent>
                    </Select>
                  </div>

                  {/* Language Selector */}
                  <div className="tf-languages">
                    <Select defaultValue="English">
                      <SelectTrigger className="w-fit border-none shadow-none hover:bg-gray-50 transition-colors duration-200">
                        <SelectValue placeholder="Select a language" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="English">English</SelectItem>
                        <SelectItem value="French">French</SelectItem>
                        <SelectItem value="German">German</SelectItem>
                        <SelectItem value="Spanish">Spanish</SelectItem>
                        <SelectItem value="Italian">Italian</SelectItem>
                        <SelectItem value="Swedish">Swedish</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                </div>
              </div>

              {/* Mobile Menu Button (Visible on mobile/tablet) */}
              <div className="flex md:w-1/3 w-1/4 xl:hidden">
                <Sheet>
                  <SheetTrigger asChild>
                    <Button variant="ghost" size="icon" className="text-xl">
                      <HiMenuAlt2 />
                    </Button>
                  </SheetTrigger>
                  <SheetContent side="left">
                    <div className="flex flex-col gap-6 py-4 min-h-screen bg-white">
                      {/* Currency Section */}
                      <div className="flex flex-col gap-2">
                        <h3 className="font-semibold">Currency</h3>
                        <Select defaultValue="USD">
                          <SelectTrigger>
                            <SelectValue />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="USD">USD $ | United States</SelectItem>
                            <SelectItem value="EUR">EUR € | Europe</SelectItem>
                          </SelectContent>
                        </Select>
                      </div>

                      {/* Language Section */}
                      <div className="flex flex-col gap-2">
                        <h3 className="font-semibold">Language</h3>
                        <Select defaultValue="English">
                          <SelectTrigger>
                            <SelectValue />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="English">English</SelectItem>
                            <SelectItem value="French">French</SelectItem>
                            <SelectItem value="German">German</SelectItem>
                            <SelectItem value="Spanish">Spanish</SelectItem>
                            <SelectItem value="Italian">Italian</SelectItem>
                            <SelectItem value="Swedish">Swedish</SelectItem>
                          </SelectContent>
                        </Select>
                      </div>

                      {/* Navigation Section */}
                      <div className="flex flex-col gap-4">
                        <h3 className="font-semibold">Navigation</h3>
                        <div className="flex flex-col gap-3">
                          <Button variant="ghost" className="justify-start text-left h-12">
                            <CiSearch className="mr-3 text-xl" />
                            <span>Search</span>
                          </Button>
                          <Button variant="ghost" className="justify-start text-left h-12">
                            <IoPersonOutline className="mr-3 text-xl" />
                            <span>Account</span>
                          </Button>
                        </div>
                      </div>
                    </div>
                  </SheetContent>
                </Sheet>
              </div>

              {/* Center Section - Logo */}
              <div className="flex md:w-1/3 w-1/2 justify-center">
                <Link href="/" className="logo-header">
                  <span className="text-2xl font-bold">nexora</span>
                </Link>
              </div>

              {/* Right Section - Navigation Icons */}
              <div className="flex md:w-1/3 w-1/4 justify-end">
                <nav className="flex items-center gap-5">
                  
                  {/* Desktop Only - Search */}
                  <div className="hidden xl:block">
                    <Tooltip>
                      <TooltipTrigger asChild>
                        <Button variant="ghost" size="icon" className="text-xl">
                          <CiSearch />
                        </Button>
                      </TooltipTrigger>
                      <TooltipContent>
                        <p>Search</p>
                      </TooltipContent>
                    </Tooltip>
                  </div>

                  {/* Desktop Only - Account */}
                  <div className="hidden xl:block">
                    <Tooltip>
                      <TooltipTrigger asChild>
                        <Button variant="ghost" size="icon" className="text-xl">
                          <IoPersonOutline />
                        </Button>
                      </TooltipTrigger>
                      <TooltipContent>
                        <p>Account</p>
                      </TooltipContent>
                    </Tooltip>
                  </div>

                  {/* Wishlist - Always Visible */}
                  <Tooltip>
                    <TooltipTrigger asChild>
                      <div className="relative">
                        <Button variant="ghost" size="icon" className="text-xl">
                          <CiHeart />
                        </Button>
                        <Badge 
                          variant="destructive" 
                          className="absolute -top-1 -right-1 h-5 w-5 rounded-full p-0 flex items-center justify-center text-xs font-semibold"
                        >
                          0
                        </Badge>
                      </div>
                    </TooltipTrigger>
                    <TooltipContent>
                      <p>Wishlist</p>
                    </TooltipContent>
                  </Tooltip>

                  {/* Shopping Cart - Always Visible */}
                  <Tooltip>
                    <TooltipTrigger asChild>
                      <div className="relative">
                        <Button variant="ghost" size="icon" className="text-xl">
                          <SlBag />
                        </Button>
                        <Badge 
                          variant="destructive" 
                          className="absolute -top-1 -right-1 h-5 w-5 rounded-full p-0 flex items-center justify-center text-xs font-semibold"
                        >
                          0
                        </Badge>
                      </div>
                    </TooltipTrigger>
                    <TooltipContent>
                      <p>Shopping Cart</p>
                    </TooltipContent>
                  </Tooltip>

                </nav>
              </div>
            </div>
          </div>
        </div>


      </header>
    </div>
  );
}
